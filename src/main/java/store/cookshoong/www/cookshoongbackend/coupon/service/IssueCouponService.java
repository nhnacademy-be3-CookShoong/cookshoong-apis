package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequest;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockInterruptedException;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockOverWaitTimeException;

/**
 * 쿠폰 발급 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.07.17
 */
@Service
@Transactional
@RequiredArgsConstructor
public class IssueCouponService {
    private static final int LIMIT_COUNT = 1_000;
    private static final int WAIT_TIME = 60;
    private static final int LEASE_TIME = 5;

    private final Map<IssueMethod, BiConsumer<CouponPolicy, Long>> issueMethodConsumer = createIssueConsumer();

    private final IssueCouponRepository issueCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final AccountRepository accountRepository;
    private final RedissonClient redissonClient;

    private Map<IssueMethod, BiConsumer<CouponPolicy, Long>> createIssueConsumer() {
        Map<IssueMethod, BiConsumer<CouponPolicy, Long>> enumMap = new EnumMap<>(IssueMethod.class);
        enumMap.put(IssueMethod.BULK, this::createIssueCouponAllAccounts);
        enumMap.put(IssueMethod.EVENT, this::createIssueCouponFirstComeFirstServe);
        enumMap.put(IssueMethod.NORMAL, this::createIssueCouponInLimitCount);

        return enumMap;
    }

    /**
     * 쿠폰 발행 메서드.
     * 요청된 개수만큼 발행시킨다.
     *
     * @param createIssueCouponRequestDto the issue coupon request dto
     */
    public void createIssueCoupon(CreateIssueCouponRequestDto createIssueCouponRequestDto) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(createIssueCouponRequestDto.getCouponPolicyId())
            .orElseThrow(CouponPolicyNotFoundException::new);

        IssueMethod issueMethod = createIssueCouponRequestDto.getIssueMethod();

        couponPolicy.getCouponUsage()
            .validIssueMethod(issueMethod);

        issueMethodConsumer.get(issueMethod)
            .accept(couponPolicy, createIssueCouponRequestDto.getIssueQuantity());
    }

    private void createIssueCouponAllAccounts(CouponPolicy couponPolicy, Long issueQuantity) {
        // TODO: Spring Batch 적용 이후 이용해볼 것
    }

    private void createIssueCouponFirstComeFirstServe(CouponPolicy couponPolicy, Long issueQuantity) {
        Set<IssueCoupon> issueCoupons = Stream.generate(() -> new IssueCoupon(couponPolicy))
            .limit(issueQuantity)
            .collect(Collectors.toSet());

        issueCouponRepository.saveAll(issueCoupons);
    }

    private void createIssueCouponInLimitCount(CouponPolicy couponPolicy, Long issueQuantity) {
        checkUnclaimedCouponCount(couponPolicy.getId(), issueQuantity);

        Stream.generate(() -> new IssueCoupon(couponPolicy))
            .limit(issueQuantity)
            .forEach(issueCouponRepository::save);
    }

    /**
     * 유저가 아직 수령하지 않은 유효 쿠폰 개수 + 발행 요청 개수가 limitCount 초과하는지 확인.
     *
     * @param couponPolicyId 쿠폰 정책 id
     * @param issueQuantity  발행 요청 개수
     */
    private void checkUnclaimedCouponCount(Long couponPolicyId, Long issueQuantity) {
        Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicyId);

        if (LIMIT_COUNT < unclaimedCouponCount + issueQuantity) {
            throw new IssueCouponOverCountException(LIMIT_COUNT);
        }
    }

    /**
     * 사용자가 쿠폰 발급을 요청했을 때, 해당 쿠폰 정책과 일치하는 쿠폰 중 하나를 발급해준다.
     *
     * @param updateProvideCouponRequest the offer coupon request
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void provideCouponToAccountByApi(UpdateProvideCouponRequest updateProvideCouponRequest) {
        Long accountId = updateProvideCouponRequest.getAccountId();

        CouponPolicy couponPolicy = couponPolicyRepository.findById(updateProvideCouponRequest.getCouponPolicyId())
            .orElseThrow(CouponPolicyNotFoundException::new);

        validBeforeProvide(accountId, couponPolicy);

        List<IssueCoupon> issueCoupons = issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(couponPolicy);
        isIssueCouponsEmpty(issueCoupons);

        LocalDate expirationDate = LocalDate.now().plusDays(couponPolicy.getUsagePeriod());
        Account account = accountRepository.getReferenceById(accountId);

        provideCoupon(issueCoupons, expirationDate, account);
    }

    private static void validPolicyDeleted(CouponPolicy couponPolicy) {
        if (couponPolicy.isDeleted()) {
            throw new CouponPolicyNotFoundException();
        }
    }

    private void validReceivedBefore(Long accountId, CouponPolicy couponPolicy) {
        if (issueCouponRepository.isReceivedBefore(couponPolicy.getId(), accountId, couponPolicy.getUsagePeriod())) {
            throw new AlreadyHasCouponWithinSamePolicyException();
        }
    }

    private static void isIssueCouponsEmpty(List<IssueCoupon> issueCoupons) {
        if (issueCoupons.isEmpty()) {
            throw new IssueCouponNotFoundException();
        }
    }

    private void provideCoupon(List<IssueCoupon> issueCoupons, LocalDate expirationDate, Account account) {
        for (IssueCoupon issueCoupon : issueCoupons) {
            if (issueCouponRepository.provideCouponToAccount(issueCoupon.getCode(), expirationDate, account)) {
                return;
            }
        }

        throw new ProvideIssueCouponFailureException();
    }

    private void validBeforeProvide(Long accountId, CouponPolicy couponPolicy) {
        validPolicyDeleted(couponPolicy);
        validReceivedBefore(accountId, couponPolicy);
    }

    /**
     * 이벤트를 통해 사용자에게 쿠폰을 발급하는 메서드.
     *
     * @param updateProvideCouponRequest the update provide coupon request
     */
    public void provideCouponToAccountByEvent(UpdateProvideCouponRequest updateProvideCouponRequest) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(updateProvideCouponRequest.getCouponPolicyId())
            .orElseThrow(CouponPolicyNotFoundException::new);

        validBeforeProvide(updateProvideCouponRequest.getAccountId(), couponPolicy);

        Account account = accountRepository.findById(updateProvideCouponRequest.getAccountId())
            .orElseThrow(UserNotFoundException::new);

        provideCouponUsingLock(couponPolicy, account);
    }

    private void provideCouponUsingLock(CouponPolicy couponPolicy, Account account) {
        RLock lock = redissonClient.getLock(couponPolicy.getId().toString());

        try {
            boolean isLocked = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new LockOverWaitTimeException();
            }

            IssueCoupon issueCoupon = issueCouponRepository.findByCouponPolicyAndAccountIsNull(couponPolicy)
                .orElseThrow(CouponExhaustionException::new);
            issueCoupon.provideToAccount(account);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException();

        } finally {
            lock.unlock();
        }
    }
}
