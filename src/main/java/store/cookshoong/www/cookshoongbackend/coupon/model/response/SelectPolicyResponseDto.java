package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;

/**
 * 쿠폰 정책 데이터를 클라이언트에게 전달하는 dto.
 *
 * @author eora21(김주호)
 * @since 2023.07.14
 */
@Getter
@AllArgsConstructor
public class SelectPolicyResponseDto {
    private Long id;
    private CouponTypeResponse couponTypeResponse;
    private String name;
    private String description;
    private LocalTime expirationTime;
    private final Long unclaimedCouponCount;
    private final Long issueCouponCount;
}
