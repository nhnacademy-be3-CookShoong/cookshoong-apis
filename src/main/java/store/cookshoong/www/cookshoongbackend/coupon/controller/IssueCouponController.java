package store.cookshoong.www.cookshoongbackend.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.IssueCouponService;

/**
 * 쿠폰 발급 엔드포인트.
 *
 * @author eora21(김주호)
 * @since 2023.07.17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/issue")
public class IssueCouponController {
    private final IssueCouponService issueCouponService;

    @PostMapping
    public void postIssueCoupon(@RequestBody CreateIssueCouponRequestDto createIssueCouponRequestDto) {
        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);
    }
}
