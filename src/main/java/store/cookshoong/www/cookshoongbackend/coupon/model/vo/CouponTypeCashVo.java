package store.cookshoong.www.cookshoongbackend.coupon.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;

/**
 * 금액 쿠폰 타입 정보를 담을 vo.
 *
 * @author eora21
 * @since 2023.07.06
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponTypeCashVo implements CouponTypeResponse {
    private int discountAmount;
    private int minimumOrderPrice;

    public static CouponTypeCashVo newInstance(CouponTypeCash couponTypeCash) {
        return new CouponTypeCashVo(couponTypeCash.getDiscountAmount(), couponTypeCash.getMinimumOrderPrice());
    }
}
