package store.cookshoong.www.cookshoongbackend.coupon.model.vo;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;

/**
 * 퍼센트 쿠폰 타입 정보를 담을 vo.
 *
 * @author eora21
 * @since 2023.07.06
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponTypePercentVo implements CouponTypeResponse {
    private BigDecimal rate;
    private int minimumPrice;
    private int maximumPrice;

    public static CouponTypePercentVo newInstance(CouponTypePercent couponTypePercent) {
        return new CouponTypePercentVo(couponTypePercent.getRate(), couponTypePercent.getMinimumPrice(),
            couponTypePercent.getMaximumPrice());
    }
}
