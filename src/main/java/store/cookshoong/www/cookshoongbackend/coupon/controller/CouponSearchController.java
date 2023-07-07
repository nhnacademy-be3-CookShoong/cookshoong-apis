package store.cookshoong.www.cookshoongbackend.coupon.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.CouponResponseDto;
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

    @GetMapping("/{accountId}")
    public ResponseEntity<List<CouponResponseDto>> getOwnCoupons(@PathVariable Long accountId) {
        return ResponseEntity.ok(couponSearchService.getOwnCoupons(accountId));
    }
}
