package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequest;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
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
@RequiredArgsConstructor
public class ProvideCouponService {
    private static final int WAIT_TIME = 60;
    private static final int LEASE_TIME = 5;

    private final IssueCouponRepository issueCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final AccountRepository accountRepository;
    private final RedissonClient redissonClient;

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
        Long accountId = updateProvideCouponRequest.getAccountId();

        CouponPolicy couponPolicy = couponPolicyRepository.findById(updateProvideCouponRequest.getCouponPolicyId())
            .orElseThrow(CouponPolicyNotFoundException::new);

        validBeforeProvide(accountId, couponPolicy);

        Account account = accountRepository.findById(accountId)
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
