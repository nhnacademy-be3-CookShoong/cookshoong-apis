package store.cookshoong.www.cookshoongbackend.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CouponPolicyRequest;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypePercentRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageMerchantRepository;
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
    private final CouponUsageMerchantRepository couponUsageMerchantRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    /**
     * 매장 금액 쿠폰 정책 생성.
     *
     * @param storeId the store id
     * @param dto     the creation store cash coupon request dto
     * @return the long
     */
    public Long createStoreCashCouponPolicy(Long storeId, CreateCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash = getOrCreateCouponTypeCash(dto);
        CouponUsageStore couponUsageStore = getOrCreateCouponUsageStore(storeId);

        return createCouponPolicy(couponTypeCash, couponUsageStore, dto)
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
        CouponTypePercent couponTypePercent = getOrCreateCouponTypePercent(dto);
        CouponUsageStore couponUsageStore = getOrCreateCouponUsageStore(storeId);

        return createCouponPolicy(couponTypePercent, couponUsageStore, dto)
            .getId();
    }

    /**
     * 가맹점 금액 쿠폰 정책 생성.
     *
     * @param merchantId the merchant id
     * @param dto        he creation store cash coupon request dto
     * @return the long
     */
    public Long createMerchantCashCouponPolicy(Long merchantId, CreateCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash = getOrCreateCouponTypeCash(dto);
        CouponUsageMerchant couponUsageMerchant = getOrCreateCouponUsageMerchant(merchantId);

        return createCouponPolicy(couponTypeCash, couponUsageMerchant, dto)
            .getId();
    }

    /**
     * 가맹점 포인트 쿠폰 정책 생성.
     *
     * @param merchantId the merchant id
     * @param dto        he creation store point coupon request dto
     * @return the long
     */
    public Long createMerchantPercentCouponPolicy(Long merchantId, CreatePercentCouponPolicyRequestDto dto) {
        CouponTypePercent couponTypePercent = getOrCreateCouponTypePercent(dto);
        CouponUsageMerchant couponUsageMerchant = getOrCreateCouponUsageMerchant(merchantId);

        return createCouponPolicy(couponTypePercent, couponUsageMerchant, dto)
            .getId();
    }

    private CouponTypeCash getOrCreateCouponTypeCash(CreateCashCouponPolicyRequestDto dto) {
        return couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(
                dto.getDiscountAmount(), dto.getMinimumPrice())
            .orElseGet(() -> couponTypeCashRepository.save(
                CreateCashCouponPolicyRequestDto.toEntity(dto)));
    }

    private CouponTypePercent getOrCreateCouponTypePercent(CreatePercentCouponPolicyRequestDto dto) {
        return couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
                dto.getRate(), dto.getMinimumPrice(), dto.getMaximumPrice())
            .orElseGet(() -> couponTypePercentRepository.save(
                CreatePercentCouponPolicyRequestDto.toEntity(dto)));
    }

    private CouponUsageStore getOrCreateCouponUsageStore(Long storeId) {
        return couponUsageStoreRepository.findByStoreId(storeId)
            .orElseGet(() -> couponUsageStoreRepository.save(new CouponUsageStore(storeId)));
    }

    private CouponUsageMerchant getOrCreateCouponUsageMerchant(Long merchantId) {
        return couponUsageMerchantRepository.findByMerchantId(merchantId)
            .orElseGet(() -> couponUsageMerchantRepository.save(new CouponUsageMerchant(merchantId)));
    }

    private CouponPolicy createCouponPolicy(CouponType couponType, CouponUsage couponUsage, CouponPolicyRequest req) {
        return couponPolicyRepository.save(
                new CouponPolicy(couponType, couponUsage, req.getName(), req.getDescription(),
                    req.getExpirationTime()));
    }
}
