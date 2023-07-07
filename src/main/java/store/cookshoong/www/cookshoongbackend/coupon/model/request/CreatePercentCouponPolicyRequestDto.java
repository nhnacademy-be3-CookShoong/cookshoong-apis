package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String name;

    private String description;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime expirationTime;

    private BigDecimal rate;

    private int minimumPrice;

    private int maximumPrice;

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

