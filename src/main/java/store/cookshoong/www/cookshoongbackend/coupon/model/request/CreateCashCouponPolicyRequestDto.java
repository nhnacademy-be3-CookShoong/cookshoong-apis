package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String name;

    private String description;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime expirationTime;

    private int discountAmount;

    private int minimumPrice;

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

