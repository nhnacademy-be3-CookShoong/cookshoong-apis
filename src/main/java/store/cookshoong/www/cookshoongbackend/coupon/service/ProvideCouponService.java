package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLog;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLogType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyUsedCouponException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ExpiredCouponException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.NonIssuedCouponProperlyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponLogRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponRedisRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.BelowMinimumOrderPriceException;

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
    private static final String ACCOUNT_LOCK = "AccountLock: ";
    private static final int SPARE_MINUTE = 30;

    private final IssueCouponRepository issueCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final AccountRepository accountRepository;
    private final CouponRedisRepository couponRedisRepository;
    private final CouponLogRepository couponLogRepository;
    private final LockProcessor lockProcessor;

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

    private void validPolicyDeleted(CouponPolicy couponPolicy) {
        if (couponPolicy.isDeleted()) {
            throw new CouponPolicyNotFoundException();
        }
    }

    private void validReceivedBefore(Long accountId, CouponPolicy couponPolicy) {
        if (issueCouponRepository.isReceivedBefore(couponPolicy.getId(), accountId, couponPolicy.getUsagePeriod())) {
            throw new AlreadyHasCouponWithinSamePolicyException();
        }
    }

    private void isIssueCouponsEmpty(List<IssueCoupon> issueCoupons) {
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

    /**
     * 쿠폰 발급 이전 검증 메서드.
     *
     * @param accountId      the account id
     * @param couponPolicyId the coupon policy id
     */
    public void validBeforeProvide(Long accountId, Long couponPolicyId) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId)
            .orElseThrow(CouponPolicyNotFoundException::new);

        validBeforeProvide(accountId, couponPolicy);
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
        lockProcessor.lock(ACCOUNT_LOCK + updateProvideCouponRequestDto.getAccountId(), ignore ->
            provideCouponByEvent(updateProvideCouponRequestDto));
    }

    private void provideCouponByEvent(UpdateProvideCouponRequestDto updateProvideCouponRequestDto) {
        Long accountId = updateProvideCouponRequestDto.getAccountId();

        UUID couponCode = getCouponCode(updateProvideCouponRequestDto.getCouponPolicyId(), accountId);

        IssueCoupon issueCoupon = issueCouponRepository.findById(couponCode)
            .orElseThrow(IssueCouponNotFoundException::new);

        if (Objects.nonNull(issueCoupon.getAccount())) {
            throw new ProvideIssueCouponFailureException();
        }

        Account account = accountRepository.getReferenceById(accountId);
        issueCouponRepository.provideCouponToAccount(issueCoupon, account);
    }

    private UUID getCouponCode(Long couponPolicyId, Long accountId) {
        String key = couponPolicyId.toString();

        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponPolicyId)
            .orElseThrow(CouponPolicyNotFoundException::new);

        validBeforeProvide(accountId, couponPolicy);

        BoundSetOperations<String, Object> issuableCouponCodes = couponRedisRepository.getRedisSet(key);
        String couponCode = (String) issuableCouponCodes.pop();

        if (Objects.isNull(couponCode)) {
            couponCode = updateCouponCode(couponPolicyId, key, issuableCouponCodes);
        }

        return UUID.fromString(couponCode);
    }

    private String updateCouponCode(Long couponPolicyId, String key,
                                    BoundSetOperations<String, Object> issuableCouponCodes) {
        Long unclaimedCouponCount = couponPolicyRepository.lookupUnclaimedCouponCount(couponPolicyId);
        validUnclaimedCoupon(unclaimedCouponCount, key);
        return updateCouponCode(issuableCouponCodes);
    }

    private String updateCouponCode(BoundSetOperations<String, Object> issuableCouponCodes) {
        String couponCode = (String) issuableCouponCodes.pop();

        if (Objects.isNull(couponCode)) {
            throw new CouponExhaustionException();
        }

        return couponCode;
    }

    private void validUnclaimedCoupon(Long unclaimedCouponCount, String key) {
        if (unclaimedCouponCount == 0) {
            throw new CouponExhaustionException();
        }

        lockProcessor.lock(key, this::updateRedisCouponState);
    }

    private void updateRedisCouponState(String key) {
        if (!couponRedisRepository.hasKey(key)) {
            Set<UUID> couponCodes = issueCouponRepository.lookupUnclaimedCouponCodes();
            couponRedisRepository.bulkInsertCouponCode(couponCodes, key);
        }
    }

    /**
     * 발급 쿠폰 검증 메서드.
     *
     * @param issueCouponCode the issue coupon
     * @param accountId       the account id
     */
    public void validProvideCoupon(UUID issueCouponCode, Long accountId) {
        IssueCoupon issueCoupon = issueCouponRepository.findById(issueCouponCode)
            .orElseThrow(IssueCouponNotFoundException::new);

        couponLogRepository.findTopByIssueCouponOrderByIdDesc(issueCoupon)
            .ifPresent(this::validRecentCouponLog);

        Account account = issueCoupon.getAccount();

        if (Objects.isNull(account) || !account.getId().equals(accountId)) {
            throw new NonIssuedCouponProperlyException();
        }
    }

    private void validRecentCouponLog(CouponLog couponLog) {
        if (couponLog.getCouponLogType().getCode().equals(CouponLogType.Code.USE.toString())) {
            throw new AlreadyUsedCouponException();
        }
    }

    /**
     * 주문 최소 금액 검증 메서드.
     *
     * @param issueCouponCode the issue coupon code
     * @param totalPrice      the total price
     */
    public void validMinimumOrderPrice(UUID issueCouponCode, int totalPrice) {
        IssueCoupon issueCoupon = issueCouponRepository.findById(issueCouponCode)
            .orElseThrow(IssueCouponNotFoundException::new);

        Integer minimumOrderPrice = issueCoupon.getCouponPolicy()
            .getCouponType()
            .getMinimumOrderPrice();

        if (totalPrice < minimumOrderPrice) {
            throw new BelowMinimumOrderPriceException();
        }
    }

    /**
     * 만료 시간 검증 메서드.
     * 30분의 추가 시간을 제공.
     *
     * @param issueCouponCode the issue coupon code
     */
    public void validExpirationDateTime(UUID issueCouponCode) {
        IssueCoupon issueCoupon = issueCouponRepository.findById(issueCouponCode)
            .orElseThrow(IssueCouponNotFoundException::new);

        LocalDateTime expirationDateTime = issueCoupon.getExpirationDate().atTime(LocalTime.MAX);
        if (LocalDateTime.now().isAfter(expirationDateTime.plusMinutes(SPARE_MINUTE))) {
            throw new ExpiredCouponException();
        }
    }

    /**
     * 할인된 금액 획득.
     *
     * @param issueCouponCode the issue coupon code
     * @param totalPrice      the total price
     * @return the discount price
     */
    public int getDiscountPrice(UUID issueCouponCode, int totalPrice) {
        IssueCoupon issueCoupon = issueCouponRepository.findById(issueCouponCode)
            .orElseThrow(IssueCouponNotFoundException::new);

        return issueCoupon.getCouponPolicy()
            .getCouponType()
            .getDiscountPrice(totalPrice);
    }
}
