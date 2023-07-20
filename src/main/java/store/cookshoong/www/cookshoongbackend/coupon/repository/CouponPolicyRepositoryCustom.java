package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.SelectPolicyResponseTempDto;

/**
 * QueryDSL 사용을 위한 interface.
 *
 * @author eora21(김주호)
 * @since 2023.07.14
 */
public interface CouponPolicyRepositoryCustom {
    /**
     * 특정 매장의 쿠폰 정책을 확인하기 위한 메서드.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return the page
     */
    Page<SelectPolicyResponseTempDto> lookupStorePolicy(Long storeId, Pageable pageable);

    /**
     * 특정 가맹점의 쿠폰 정책을 확인하기 위한 메서드.
     *
     * @param merchantId the merchant id
     * @param pageable   the pageable
     * @return the page
     */
    Page<SelectPolicyResponseTempDto> lookupMerchantPolicy(Long merchantId, Pageable pageable);

    /**
     * 모든 사용처의 쿠폰 정책을 확인하기 위한 메서드.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<SelectPolicyResponseTempDto> lookupAllPolicy(Pageable pageable);

    /**
     * 사용자가 받지 않은 쿠폰 개수 확인 메서드.
     *
     * @param couponPolicyId the coupon policy id
     * @return the long
     */
    Long lookupUnclaimedCouponCount(Long couponPolicyId);
}
