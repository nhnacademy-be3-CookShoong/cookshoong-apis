package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponRedisRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockInterruptedException;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockOverWaitTimeException;

/**
 * 쿠폰 발급 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.07.30
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProvideCouponService {
    private static final String EXPLAIN = "couponPolicyId -";
    private static final String NOT_MATCH_LOCK = "- IsNotMatch";
    private static final int WAIT_TIME = 60;
    private static final int LEASE_TIME = 5;

    private final IssueCouponRepository issueCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final AccountRepository accountRepository;
    private final RedissonClient redissonClient;
    private final CouponRedisRepository couponRedisRepository;

    /**
     * 사용자가 쿠폰 발급을 요청했을 때, 해당 쿠폰 정책과 일치하는 쿠폰 중 하나를 발급해준다.
     *
     * @param updateProvideCouponRequestDto the offer coupon request
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void provideCouponToAccountByApi(UpdateProvideCouponRequestDto updateProvideCouponRequestDto) {
        Long accountId = updateProvideCouponRequestDto.getAccountId();

        CouponPolicy couponPolicy = couponPolicyRepository.findById(updateProvideCouponRequestDto.getCouponPolicyId())
                .orElseThrow(CouponPolicyNotFoundException::new);

        validBeforeProvide(accountId, couponPolicy);

        List<IssueCoupon> issueCoupons = issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(couponPolicy);
        isIssueCouponsEmpty(issueCoupons);

        Account account = accountRepository.getReferenceById(accountId);

        provideCoupon(issueCoupons, account);
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
            throw new CouponExhaustionException();
        }
    }

    private void provideCoupon(List<IssueCoupon> issueCoupons, Account account) {
        for (IssueCoupon issueCoupon : issueCoupons) {
            try {
                issueCouponRepository.provideCouponToAccount(issueCoupon, account);
                return;
            } catch (ProvideIssueCouponFailureException ignore) {
                // ignore
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
     * @param updateProvideCouponRequestDto the update provide coupon request
     */
    public void provideCouponToAccountByEvent(UpdateProvideCouponRequestDto updateProvideCouponRequestDto) {
        Long couponPolicyId = updateProvideCouponRequestDto.getCouponPolicyId();
        String key = couponPolicyId.toString();

        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(CouponPolicyNotFoundException::new);

        Long accountId = updateProvideCouponRequestDto.getAccountId();
        validBeforeProvide(accountId, couponPolicy);

        BoundSetOperations<String, Object> couponIds = couponRedisRepository.getRedisSet(key);
        String couponId = (String) couponIds.pop();

        if (Objects.isNull(couponId)) {
            Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicyId);
            validUnclaimedCoupon(unclaimedCouponCount, key);
            couponId = updateCouponId(couponIds);
        }

        IssueCoupon issueCoupon = issueCouponRepository.findById(UUID.fromString(couponId))
                .orElseThrow(IssueCouponNotFoundException::new);

        if (Objects.nonNull(issueCoupon.getAccount())) {
            throw new ProvideIssueCouponFailureException();
        }

        Account account = accountRepository.getReferenceById(accountId);
        issueCouponRepository.provideCouponToAccount(issueCoupon, account);
    }

    private static String updateCouponId(BoundSetOperations<String, Object> couponIds) {
        String couponId = (String) couponIds.pop();

        if (Objects.isNull(couponId)) {
            throw new CouponExhaustionException();
        }

        return couponId;
    }

    private void validUnclaimedCoupon(Long unclaimedCouponCount, String key) {
        if (unclaimedCouponCount == 0) {
            throw new CouponExhaustionException();
        }

        lock(key, this::updateRedisCouponState);
    }

    private void lock(String key, Consumer<String> consumer) {
        RLock lock = redissonClient.getLock(EXPLAIN + key + NOT_MATCH_LOCK);

        try {
            boolean lockSuccess = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            if (!lockSuccess) {
                throw new LockOverWaitTimeException();
            }

            consumer.accept(key);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException();
        } finally {
            lock.unlock();
        }
    }

    private void updateRedisCouponState(String key) {
        if (!couponRedisRepository.hasKey(key)) {
            Set<UUID> couponCodes = issueCouponRepository.lookupUnclaimedCouponCodes();
            couponRedisRepository.bulkInsertCouponCode(couponCodes, key);
        }
    }
}
