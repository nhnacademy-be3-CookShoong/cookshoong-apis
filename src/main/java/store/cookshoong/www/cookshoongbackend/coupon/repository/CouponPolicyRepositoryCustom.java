package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableStoreCouponPolicyResponseDto;

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
    Page<SelectPolicyResponseDto> lookupStorePolicy(Long storeId, Pageable pageable);

    /**
     * 특정 가맹점의 쿠폰 정책을 확인하기 위한 메서드.
     *
     * @param merchantId the merchant id
     * @param pageable   the pageable
     * @return the page
     */
    Page<SelectPolicyResponseDto> lookupMerchantPolicy(Long merchantId, Pageable pageable);

    /**
     * 모든 사용처의 쿠폰 정책을 확인하기 위한 메서드.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<SelectPolicyResponseDto> lookupAllPolicy(Pageable pageable);

    /**
     * 사용자가 받지 않은 쿠폰 개수 확인 메서드.
     *
     * @param couponPolicyId the coupon policy id
     * @return the long
     */
    Long lookupUnclaimedCouponCount(Long couponPolicyId);

    /**
     * 매장에서 발급 가능한 쿠폰 정책 목록을 제공하는 메서드.
     *
     * @param storeId the store id
     * @return the list
     */
    List<SelectProvableStoreCouponPolicyResponseDto> lookupProvableStoreCouponPolicies(Long storeId);
}
