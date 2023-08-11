package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;

/**
 * 쿠폰 발행 요청 시 사용되는 dto.
 *
 * @author eora21(김주호)
 * @since 2023.07.17
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateIssueCouponRequestDto {
    @NotNull
    private Long couponPolicyId;

    @NotNull
    @Min(1)
    private Long issueQuantity;

    @NotNull
    private IssueMethod issueMethod;
}
