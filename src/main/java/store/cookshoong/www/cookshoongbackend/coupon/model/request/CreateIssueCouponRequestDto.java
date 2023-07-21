package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쿠폰 발급 요청 시 사용되는 dto.
 *
 * @author eora21(김주호)
 * @since 2023.07.17
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateIssueCouponRequestDto {
    @NotNull
    @Min(1)
    private Long issueQuantity;

    @NotNull
    private Long couponPolicyId;
}
