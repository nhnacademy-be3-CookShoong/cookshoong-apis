package store.cookshoong.www.cookshoongbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateStoreCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
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
    private final CouponUsageStoreRepository couponUsageStoreRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    /**
     * 매장 금액 쿠폰 정책 생성.
     *
     * @param storeId the store id
     * @param dto     the creation store cash coupon request dto
     */
    public Long createStoreCashCouponPolicy(Long storeId, CreateStoreCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash =
            couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(dto.getDiscountAmount(), dto.getMinimumPrice())
                .orElseGet(() -> couponTypeCashRepository.save(
                    CreateStoreCashCouponPolicyRequestDto.toCouponTypeCash(dto)));

        CouponUsageStore couponUsageStore =
            couponUsageStoreRepository.findByStoreId(storeId)
                .orElseGet(() -> couponUsageStoreRepository.save(new CouponUsageStore(storeId)));

        return couponPolicyRepository.save(
                CreateStoreCashCouponPolicyRequestDto.toCouponPolicy(couponTypeCash, couponUsageStore, dto))
            .getId();
    }
}
