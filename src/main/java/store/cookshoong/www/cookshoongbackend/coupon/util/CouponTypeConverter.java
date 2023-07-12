package store.cookshoong.www.cookshoongbackend.coupon.util;

import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.CouponTypeCashResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.CouponTypePercentResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.CouponTypeResponse;

/**
 * 업캐스팅 쿠폰 타입 객체를 다운캐스팅하는 Converter.
 *
 * @author eora21
 * @since 2023.07.06
 */
@Component
public class CouponTypeConverter {
    private static final Map<Class<? extends CouponType>, Function<CouponType, CouponTypeResponse>> COUPON_TYPE_MAPPER =
        Map.of(
            CouponTypeCash.class, CouponTypeConverter::couponTypeCashToDto,
            CouponTypePercent.class, CouponTypeConverter::couponTypePercentToDto
        );

    private static CouponTypeResponse couponTypeCashToDto(CouponType couponTypeCash) {
        return CouponTypeCashResponseDto.newInstance((CouponTypeCash) couponTypeCash);
    }

    private static CouponTypeResponse couponTypePercentToDto(CouponType couponTypePercent) {
        return CouponTypePercentResponseDto.newInstance((CouponTypePercent) couponTypePercent);
    }

    public CouponTypeResponse convert(CouponType couponType) {
        return COUPON_TYPE_MAPPER.get(couponType.getClass())
            .apply(couponType);
    }
}
