package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;

/**
 * 금액 쿠폰 타입 정보를 담을 dto.
 *
 * @author eora21
 * @since 2023.07.06
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponTypeCashResponseDto implements CouponTypeResponse {
    private int discountAmount;
    private int minimumPrice;

    public static CouponTypeCashResponseDto newInstance(CouponTypeCash couponTypeCash) {
        return new CouponTypeCashResponseDto(couponTypeCash.getDiscountAmount(), couponTypeCash.getMinimumPrice());
    }
}
