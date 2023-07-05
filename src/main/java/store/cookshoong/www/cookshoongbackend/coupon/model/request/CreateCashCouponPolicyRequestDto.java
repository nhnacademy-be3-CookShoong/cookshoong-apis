package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;

/**
 * 가게에서 금액 쿠폰 정책을 생성할 때 사용되는 dto.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
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
    public static CouponTypeCash toCouponTypeCash(CreateCashCouponPolicyRequestDto dto) {
        return new CouponTypeCash(dto.getDiscountAmount(), dto.getMinimumPrice());
    }

    /**
     * dto, 쿠폰 타입, 쿠폰 사용처를 이용하여 CouponPolicy 엔티티를 생성하는 static method.
     *
     * @param couponTypeCash the coupon type cash
     * @param couponUsage    the coupon usage
     * @param dto            the dto
     * @return the coupon policy
     */
    public static CouponPolicy toCouponPolicy(CouponTypeCash couponTypeCash, CouponUsage couponUsage,
                                              CreateCashCouponPolicyRequestDto dto) {
        return new CouponPolicy(couponTypeCash, couponUsage, dto.getName(), dto.getDescription(),
            dto.getExpirationTime());
    }
}

