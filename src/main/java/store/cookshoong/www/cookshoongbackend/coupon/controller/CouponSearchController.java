package store.cookshoong.www.cookshoongbackend.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponSearchService;

/**
 * 쿠폰 조회 RestController.
 * 소유한 쿠폰을 검색하며, 클라이언트의 요청에 따라 필터링하여 보여줄 수 있도록 한다.
 *
 * @author eora21
 * @since 2023.07.06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/search")
public class CouponSearchController {
    private final CouponSearchService couponSearchService;

    /**
     * 소유한 쿠폰 반환하는 엔드포인트.
     *
     * @param accountId the account id
     * @param pageable  페이징 설정
     * @param usable   사용 가능 여부 컨디션(null = 사용 가능•불가 쿠폰 모두, true = 사용 가능, false = 사용 불가)
     * @param storeId   the store id
     * @return 소유 쿠폰 중 사용 가능 등 필터링한 결과
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Page<SelectOwnCouponResponseDto>> getOwnCoupons(
        @PathVariable Long accountId, Pageable pageable, @RequestParam Boolean usable, @RequestParam Long storeId) {

        return ResponseEntity.ok(couponSearchService.getOwnCoupons(accountId, pageable, usable, storeId));
    }
}
