package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쿠폰 발급 요청 시 사용되는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.07.21
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProvideCouponRequest {
    @NotNull
    private Long couponPolicyId;
}
