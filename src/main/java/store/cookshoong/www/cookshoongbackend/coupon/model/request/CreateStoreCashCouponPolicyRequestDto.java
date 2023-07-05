package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;

/**
 * 가게에서 쿠폰 CASH 정책을 생성할 때 사용되는 dto.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
public class CreateStoreCashCouponPolicyRequestDto {
    private String name;

    private String description;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime expirationTime;

    private int discountAmount;

    private int minimumPrice;

    /**
     * To coupon type cash coupon type cash.
     *
     * @param dto the dto
     * @return the coupon type cash
     */
    public static CouponTypeCash toCouponTypeCash(CreateStoreCashCouponPolicyRequestDto dto) {
        return new CouponTypeCash(dto.getDiscountAmount(), dto.getMinimumPrice());
    }

    /**
     * To coupon policy coupon policy.
     *
     * @param couponTypeCash the coupon type cash
     * @param couponUsage    the coupon usage
     * @param dto            the dto
     * @return the coupon policy
     */
    public static CouponPolicy toCouponPolicy(CouponTypeCash couponTypeCash, CouponUsage couponUsage,
                                              CreateStoreCashCouponPolicyRequestDto dto) {
        return new CouponPolicy(couponTypeCash, couponUsage, dto.getName(), dto.getDescription(),
            dto.getExpirationTime());
    }
}

