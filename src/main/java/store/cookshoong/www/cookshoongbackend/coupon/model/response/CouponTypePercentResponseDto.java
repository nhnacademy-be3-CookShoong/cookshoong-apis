package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;

/**
 * 퍼센트 쿠폰 타입 정보를 담을 dto.
 *
 * @author eora21
 * @since 2023.07.06
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponTypePercentResponseDto implements CouponTypeResponse {
    private BigDecimal rate;
    private int minimumPrice;
    private int maximumPrice;

    public static CouponTypePercentResponseDto newInstance(CouponTypePercent couponTypePercent) {
        return new CouponTypePercentResponseDto(couponTypePercent.getRate(), couponTypePercent.getMinimumPrice(),
            couponTypePercent.getMaximumPrice());
    }
}
