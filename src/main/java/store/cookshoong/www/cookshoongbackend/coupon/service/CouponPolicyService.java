package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponUsageNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.AbstractCouponPolicyRequest;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableStoreCouponPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypePercentRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageAllRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageMerchantRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageStoreRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 쿠폰 정책 서비스.
 * 쿠폰 타입과 사용처가 없다면 추가하여 정책을 만든다.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CouponPolicyService {
    private final CouponTypeCashRepository couponTypeCashRepository;
    private final CouponTypePercentRepository couponTypePercentRepository;
    private final CouponUsageStoreRepository couponUsageStoreRepository;
    private final CouponUsageMerchantRepository couponUsageMerchantRepository;
    private final CouponUsageAllRepository couponUsageAllRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final StoreRepository storeRepository;
    private final MerchantRepository merchantRepository;

    /**
     * 매장 정책 확인.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return the page
     */
    public Page<SelectPolicyResponseDto> selectStorePolicy(Long storeId, Pageable pageable) {
        return couponPolicyRepository.lookupStorePolicy(storeId, pageable);
    }

    /**
     * 가맹점 정책 확인.
     *
     * @param merchantId the merchant id
     * @param pageable   the pageable
     * @return the page
     */
    public Page<SelectPolicyResponseDto> selectMerchantPolicy(Long merchantId, Pageable pageable) {
        return couponPolicyRepository.lookupMerchantPolicy(merchantId, pageable);
    }

    /**
     * 모든 사용처 정책 확인.
     *
     * @param pageable the pageable
     * @return the page
     */
    public Page<SelectPolicyResponseDto> selectUsageAllPolicy(Pageable pageable) {
        return couponPolicyRepository.lookupAllPolicy(pageable);
    }

    /**
     * 매장 금액 쿠폰 정책 생성.
     *
     * @param storeId the store id
     * @param dto     the creation store cash coupon request dto
     */
    public void createStoreCashCouponPolicy(Long storeId, CreateCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash = getOrCreateCouponTypeCash(dto);
        CouponUsageStore couponUsageStore = getOrCreateCouponUsageStore(storeId);

        createCouponPolicy(couponTypeCash, couponUsageStore, dto);
    }

    /**
     * 매장 포인트 쿠폰 정책 생성.
     *
     * @param storeId the store id
     * @param dto     the creation store point coupon request dto
     */
    public void createStorePercentCouponPolicy(Long storeId, CreatePercentCouponPolicyRequestDto dto) {
        CouponTypePercent couponTypePercent = getOrCreateCouponTypePercent(dto);
        CouponUsageStore couponUsageStore = getOrCreateCouponUsageStore(storeId);

        createCouponPolicy(couponTypePercent, couponUsageStore, dto);
    }

    /**
     * 가맹점 금액 쿠폰 정책 생성.
     *
     * @param merchantId the merchant id
     * @param dto        he creation store cash coupon request dto
     */
    public void createMerchantCashCouponPolicy(Long merchantId, CreateCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash = getOrCreateCouponTypeCash(dto);
        CouponUsageMerchant couponUsageMerchant = getOrCreateCouponUsageMerchant(merchantId);

        createCouponPolicy(couponTypeCash, couponUsageMerchant, dto);
    }

    /**
     * 가맹점 포인트 쿠폰 정책 생성.
     *
     * @param merchantId the merchant id
     * @param dto        he creation store point coupon request dto
     */
    public void createMerchantPercentCouponPolicy(Long merchantId, CreatePercentCouponPolicyRequestDto dto) {
        CouponTypePercent couponTypePercent = getOrCreateCouponTypePercent(dto);
        CouponUsageMerchant couponUsageMerchant = getOrCreateCouponUsageMerchant(merchantId);

        createCouponPolicy(couponTypePercent, couponUsageMerchant, dto);
    }

    /**
     * 전체 금액 쿠폰 정책 생성.
     *
     * @param dto the dto
     */
    public void createAllCashCouponPolicy(CreateCashCouponPolicyRequestDto dto) {
        CouponTypeCash couponTypeCash = getOrCreateCouponTypeCash(dto);
        createCouponPolicy(couponTypeCash, getOnlyOneUsageAll(), dto);
    }

    /**
     * 전체 포인트 쿠폰 정책 생성.
     *
     * @param dto the dto
     */
    public void createAllPercentCouponPolicy(CreatePercentCouponPolicyRequestDto dto) {
        CouponTypePercent couponTypePercent = getOrCreateCouponTypePercent(dto);
        createCouponPolicy(couponTypePercent, getOnlyOneUsageAll(), dto);
    }

    private CouponTypeCash getOrCreateCouponTypeCash(CreateCashCouponPolicyRequestDto dto) {
        return couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(
                dto.getDiscountAmount(), dto.getMinimumOrderPrice())
            .orElseGet(() -> couponTypeCashRepository.save(
                CreateCashCouponPolicyRequestDto.toEntity(dto)));
    }

    private CouponTypePercent getOrCreateCouponTypePercent(CreatePercentCouponPolicyRequestDto dto) {
        return couponTypePercentRepository.findByRateAndMinimumOrderPriceAndMaximumDiscountAmount(
                dto.getRate(), dto.getMinimumOrderPrice(), dto.getMaximumDiscountAmount())
            .orElseGet(() -> couponTypePercentRepository.save(
                CreatePercentCouponPolicyRequestDto.toEntity(dto)));
    }

    private CouponUsageStore getOrCreateCouponUsageStore(Long storeId) {
        return couponUsageStoreRepository.findByStoreId(storeId)
            .orElseGet(() -> createCouponUsageStore(storeId));
    }

    private CouponUsageStore createCouponUsageStore(Long storeId) {
        Store store = storeRepository.getReferenceById(storeId);
        return couponUsageStoreRepository.save(new CouponUsageStore(store));
    }

    private CouponUsageMerchant getOrCreateCouponUsageMerchant(Long merchantId) {
        return couponUsageMerchantRepository.findByMerchantId(merchantId)
            .orElseGet(() -> createCouponUsageMerchant(merchantId));
    }

    private CouponUsageMerchant createCouponUsageMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.getReferenceById(merchantId);
        return couponUsageMerchantRepository.save(new CouponUsageMerchant(merchant));
    }

    private void createCouponPolicy(CouponType couponType, CouponUsage couponUsage, AbstractCouponPolicyRequest req) {
        couponPolicyRepository.save(new CouponPolicy(couponType, couponUsage, req.getName(), req.getDescription(),
            req.getUsagePeriod()));
    }

    /**
     * UsageAll 값은 단 하나로, 테이블에서 하나만 가져와 확인하도록 한다. 만약 값이 실수로라도 제거됐다면 예외를 내보내도록 한다.
     *
     * @return 모든 사용 범위
     */
    private CouponUsageAll getOnlyOneUsageAll() {
        return couponUsageAllRepository.findTopByOrderByIdAsc()
            .orElseThrow(CouponUsageNotFoundException::new);
    }

    /**
     * 쿠폰 정책 삭제.
     *
     * @param policyId the policy id
     */
    public void deletePolicy(Long policyId) {
        couponPolicyRepository.findById(policyId)
            .ifPresent(CouponPolicy::delete);
    }

    /**
     * 제공 가능한 쿠폰 정책 목록 전달.
     *
     * @param storeId the store id
     * @return the provable store coupon policies
     */
    public List<SelectProvableStoreCouponPolicyResponseDto> getProvableStoreCouponPolicies(Long storeId) {
        return couponPolicyRepository.lookupProvableStoreCouponPolicies(storeId);
    }
}
