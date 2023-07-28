package store.cookshoong.www.cookshoongbackend.coupon.util;

import java.util.Map;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeCashVo;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypePercentVo;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;

/**
 * 업캐스팅 쿠폰 타입 객체를 다운캐스팅하는 Converter.
 *
 * @author eora21
 * @since 2023.07.06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponTypeConverter {
    private static final Map<Class<? extends CouponType>, Function<CouponType, CouponTypeResponse>> COUPON_TYPE_MAPPER =
        Map.of(
            CouponTypeCash.class, CouponTypeConverter::couponTypeCashToDto,
            CouponTypePercent.class, CouponTypeConverter::couponTypePercentToDto
        );

    private static CouponTypeResponse couponTypeCashToDto(CouponType couponTypeCash) {
        return CouponTypeCashVo.newInstance((CouponTypeCash) couponTypeCash);
    }

    private static CouponTypeResponse couponTypePercentToDto(CouponType couponTypePercent) {
        return CouponTypePercentVo.newInstance((CouponTypePercent) couponTypePercent);
    }

    public static CouponTypeResponse convert(CouponType couponType) {
        return COUPON_TYPE_MAPPER.get(couponType.getClass())
            .apply(couponType);
    }
}
