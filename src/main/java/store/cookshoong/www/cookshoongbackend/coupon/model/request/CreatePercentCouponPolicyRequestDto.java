package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
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
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;

/**
 * 가게에서 포인트 쿠폰 정책을 생성할 때 사용되는 dto.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePercentCouponPolicyRequestDto implements CouponPolicyRequest {
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
    @DecimalMin(value = "10.0")
    @DecimalMax(value = "50.0")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal rate;

    @NotNull
    @Min(value = 0)
    @Max(value = 20_000)
    private Integer minimumPrice;

    @NotNull
    @Min(value = 5_000)
    @Max(value = 50_000)
    private Integer maximumPrice;

    /**
     * dto 값을 이용하여 CouponTypePercent 엔티티를 생성하는 static method.
     *
     * @param dto the dto
     * @return the coupon type percent
     */
    public static CouponTypePercent toEntity(CreatePercentCouponPolicyRequestDto dto) {
        return new CouponTypePercent(dto.getRate(), dto.getMinimumPrice(), dto.getMaximumPrice());
    }
}

