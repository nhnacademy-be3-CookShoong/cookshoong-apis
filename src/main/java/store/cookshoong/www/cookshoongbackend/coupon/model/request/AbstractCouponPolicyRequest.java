package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 쿠폰 정책 생성 시 동일 필드를 정의한 abstract class.
 *
 * @author eora21
 * @since 2023.07.05
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCouponPolicyRequest {
    @NotBlank
    @Length(max = 20)
    private String name;

    @NotBlank
    @Length(max = 50)
    private String description;

    @NotNull
    @Min(value = 0)
    @Max(value = 90)
    private Integer usagePeriod;
}
