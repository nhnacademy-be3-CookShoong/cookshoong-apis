package store.cookshoong.www.cookshoongbackend.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.IssueCouponService;

/**
 * 쿠폰 발행 RestController.
 *
 * @author eora21(김주호)
 * @since 2023.07.17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/issue")
public class IssueCouponController {
    private final IssueCouponService issueCouponService;

    /**
     * 쿠폰 발행 엔드포인트.
     *
     * @param createIssueCouponRequestDto 쿠폰 정책 id와 발행 개수를 담은 dto
     * @return Http Status Created
     */
    @PostMapping
    public ResponseEntity<Void> postIssueCoupon(@RequestBody CreateIssueCouponRequestDto createIssueCouponRequestDto) {
        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }
}
