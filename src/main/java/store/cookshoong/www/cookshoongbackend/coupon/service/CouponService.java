package store.cookshoong.www.cookshoongbackend.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypePercentRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageStoreRepository;

/**
 * 쿠폰 서비스.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {
    private final CouponTypeCashRepository couponTypeCashRepository;
    private final CouponTypePercentRepository couponTypePercentRepository;
    private final CouponUsageStoreRepository couponUsageStoreRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    /**
     * 매장 금액 쿠폰 정책 생성.
     *
     * @param storeId the store id
     * @param dto     the creation store cash coupon request dto
     * @return the long
     */
    public Long createStoreCashCouponPolicy(Long storeId, CreateCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash =
            couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(dto.getDiscountAmount(), dto.getMinimumPrice())
                .orElseGet(() -> couponTypeCashRepository.save(
                    CreateCashCouponPolicyRequestDto.toCouponTypeCash(dto)));

        CouponUsageStore couponUsageStore = getOrCreateCouponUsageStore(storeId);

        return couponPolicyRepository.save(
                CreateCashCouponPolicyRequestDto.toCouponPolicy(couponTypeCash, couponUsageStore, dto))
            .getId();
    }

    /**
     * 매장 포인트 쿠폰 정책 생성.
     *
     * @param storeId the store id
     * @param dto     the creation store point coupon request dto
     * @return the long
     */
    public Long createStorePercentCouponPolicy(Long storeId, CreatePercentCouponPolicyRequestDto dto) {
        CouponTypePercent couponTypePercent = couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
                dto.getRate(), dto.getMinimumPrice(), dto.getMaximumPrice())
            .orElseGet(() -> couponTypePercentRepository.save(
                CreatePercentCouponPolicyRequestDto.toCouponTypePercent(dto)));

        CouponUsageStore couponUsageStore = getOrCreateCouponUsageStore(storeId);

        return couponPolicyRepository.save(
                CreatePercentCouponPolicyRequestDto.toCouponPolicy(couponTypePercent, couponUsageStore, dto))
            .getId();
    }

    private CouponUsageStore getOrCreateCouponUsageStore(Long storeId) {
        return couponUsageStoreRepository.findByStoreId(storeId)
            .orElseGet(() -> couponUsageStoreRepository.save(new CouponUsageStore(storeId)));
    }
}
