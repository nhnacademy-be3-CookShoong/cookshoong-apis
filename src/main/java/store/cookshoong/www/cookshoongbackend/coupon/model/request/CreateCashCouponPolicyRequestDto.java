package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.time.LocalTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;

/**
 * 가게에서 금액 쿠폰 정책을 생성할 때 사용되는 dto.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCashCouponPolicyRequestDto implements CouponPolicyRequest {
    @NotBlank
    @Length(max = 20)
    private String name;

    @NotBlank
    @Length(max = 50)
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime expirationTime;

    @NotNull
    @Min(value = 5_000)
    @Max(value = 50_000)
    private Integer discountAmount;

    @NotNull
    @Min(value = 0)
    @Max(value = 20_000)
    private Integer minimumPrice;

    /**
     * dto 값을 이용하여 CouponTypeCash 엔티티를 생성하는 static method.
     *
     * @param dto the dto
     * @return the coupon type cash entity
     */
    public static CouponTypeCash toEntity(CreateCashCouponPolicyRequestDto dto) {
        return new CouponTypeCash(dto.getDiscountAmount(), dto.getMinimumPrice());
    }
}

