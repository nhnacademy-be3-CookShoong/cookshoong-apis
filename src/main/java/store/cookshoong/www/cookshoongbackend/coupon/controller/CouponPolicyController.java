package store.cookshoong.www.cookshoongbackend.coupon.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyRequestValidationException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponPolicyService;

/**
 * 쿠폰 정책 RestController.
 * 쿠폰 타입, 사용처 지정과 전체 정책을 담당한다.
 *
 * @author eora21
 * @since 2023.07.04
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/policies")
public class CouponPolicyController {
    private final CouponPolicyService couponPolicyService;

    /**
     * 매장 쿠폰 정책 조회.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return the store policy
     */
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<SelectPolicyResponseDto>> getStorePolicy(@PathVariable Long storeId, Pageable pageable) {
        return ResponseEntity.ok(couponPolicyService.selectStorePolicy(storeId, pageable));
    }

    /**
     * 가맹점 쿠폰 정책 조회.
     *
     * @param merchantId the merchant id
     * @param pageable   the pageable
     * @return the merchant policy
     */
    @GetMapping("/merchants/{merchantId}")
    public ResponseEntity<Page<SelectPolicyResponseDto>> getMerchantPolicy(@PathVariable Long merchantId,
                                                                           Pageable pageable) {
        return ResponseEntity.ok(couponPolicyService.selectMerchantPolicy(merchantId, pageable));
    }

    /**
     * 어느 곳이든 사용 가능한 쿠폰 정책 조회.
     *
     * @param pageable the pageable
     * @return the usage all policy
     */
    @GetMapping("/all")
    public ResponseEntity<Page<SelectPolicyResponseDto>> getUsageAllPolicy(Pageable pageable) {
        return ResponseEntity.ok(couponPolicyService.selectUsageAllPolicy(pageable));
    }

    /**
     * 매장 금액 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param storeId the store id
     * @param dto     가게에서 쿠폰 금액 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/stores/{storeId}/cash")
    public ResponseEntity<Void> postStoreCashCouponPolicy(@PathVariable Long storeId,
                                                          @RequestBody @Valid CreateCashCouponPolicyRequestDto dto,
                                                          BindingResult bindingResult) {
        validPolicyRequest(bindingResult);
        couponPolicyService.createStoreCashCouponPolicy(storeId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 매장 포인트 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param storeId the store id
     * @param dto     가게에서 쿠폰 포인트 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/stores/{storeId}/percent")
    public ResponseEntity<Void> postStorePercentCouponPolicy(@PathVariable Long storeId,
                                                             @RequestBody @Valid
                                                             CreatePercentCouponPolicyRequestDto dto,
                                                             BindingResult bindingResult) {
        validPolicyRequest(bindingResult);
        couponPolicyService.createStorePercentCouponPolicy(storeId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 가맹점 금액 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param merchantId the merchant id
     * @param dto        가맹점에서 쿠폰 금액 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/merchants/{merchantId}/cash")
    public ResponseEntity<Void> postMerchantCashCouponPolicy(@PathVariable Long merchantId,
                                                             @RequestBody @Valid CreateCashCouponPolicyRequestDto dto,
                                                             BindingResult bindingResult) {
        validPolicyRequest(bindingResult);
        couponPolicyService.createMerchantCashCouponPolicy(merchantId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 가맹점 포인트 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param merchantId the merchant id
     * @param dto        가맹점에서 쿠폰 포인트 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/merchants/{merchantId}/percent")
    public ResponseEntity<Void> postMerchantPointCouponPolicy(@PathVariable Long merchantId,
                                                              @RequestBody @Valid
                                                              CreatePercentCouponPolicyRequestDto dto,
                                                              BindingResult bindingResult) {
        validPolicyRequest(bindingResult);
        couponPolicyService.createMerchantPercentCouponPolicy(merchantId, dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 모든 범위 금액 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param dto 쿠폰 금액 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/all/cash")
    public ResponseEntity<Void> postAllCashCouponPolicy(@RequestBody @Valid CreateCashCouponPolicyRequestDto dto,
                                                        BindingResult bindingResult) {
        validPolicyRequest(bindingResult);
        couponPolicyService.createAllCashCouponPolicy(dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 모든 범위 포인트 쿠폰 정책 생성을 위한 엔드포인트.
     *
     * @param dto 쿠폰 포인트 정책을 생성할 때 사용되는 dto
     * @return CREATED status 및 쿠폰 정책 id
     */
    @PostMapping("/all/percent")
    public ResponseEntity<Void> postAllPointCouponPolicy(@RequestBody @Valid CreatePercentCouponPolicyRequestDto dto,
                                                         BindingResult bindingResult) {
        validPolicyRequest(bindingResult);
        couponPolicyService.createAllPercentCouponPolicy(dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    private static void validPolicyRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CouponPolicyRequestValidationException(bindingResult);
        }
    }
}
