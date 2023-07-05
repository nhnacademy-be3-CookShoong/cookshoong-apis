package store.cookshoong.www.cookshoongbackend.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponService;

/**
 * 쿠폰 RestController.
 * 쿠폰 타입, 사용처 지정과 전체 정책, 발행, 내역 확인을 담당한다.
 * 소유 쿠폰 조회가 가능하도록 작성하며, 페이징과 필터링 모두 가능하도록 한다.
 *
 * @author eora21
 * @since 2023.07.04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon")
public class CouponController {
    private final CouponService couponService;


    /**
     * 매장 금액 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param storeId the store id
     * @param dto     가게에서 쿠폰 금액 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/stores/{storeId}/policies/cash")
    public ResponseEntity<Long> createStoreCashCouponPolicy(@PathVariable Long storeId,
                                                            CreateCashCouponPolicyRequestDto dto) {
        Long storeCashCouponPolicyId = couponService.createStoreCashCouponPolicy(storeId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(storeCashCouponPolicyId);
    }

    /**
     * 매장 포인트 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param storeId the store id
     * @param dto     가게에서 쿠폰 포인트 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/stores/{storeId}/policies/percent")
    public ResponseEntity<Long> createStorePercentCouponPolicy(@PathVariable Long storeId,
                                                            CreatePercentCouponPolicyRequestDto dto) {
        Long storePercentCouponPolicyId = couponService.createStorePercentCouponPolicy(storeId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(storePercentCouponPolicyId);
    }

    /**
     * 가맹점 금액 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param merchantId the merchant id
     * @param dto        가맹점에서 쿠폰 금액 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/merchants/{merchantId}/policies/cash")
    public ResponseEntity<Long> createMerchantCashCouponPolicy(@PathVariable Long merchantId,
                                                               CreateCashCouponPolicyRequestDto dto) {
        Long storePercentCouponPolicyId = couponService.createMerchantCashCouponPolicy(merchantId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(storePercentCouponPolicyId);
    }

    /**
     * 가맹점 포인트 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param merchantId the merchant id
     * @param dto        가맹점에서 쿠폰 포인트 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/merchants/{merchantId}/policies/percent")
    public ResponseEntity<Long> createMerchantPointCouponPolicy(@PathVariable Long merchantId,
                                                                CreatePercentCouponPolicyRequestDto dto) {
        Long storePercentCouponPolicyId = couponService.createMerchantPercentCouponPolicy(merchantId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(storePercentCouponPolicyId);
    }
}
