package store.cookshoong.www.cookshoongbackend.coupon.util;

import java.util.Map;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;

/**
 * 업캐스팅 쿠폰 사용처 객체를 다운캐스팅 및 연관관계 entity 이름을 반환하는 Converter.
 *
 * @author eora21
 * @since 2023.07.06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponUsageConverter {
    private static final Map<Class<? extends CouponUsage>, Function<CouponUsage, String>> COUPON_USAGE_MAPPER = Map.of(
        CouponUsageAll.class, CouponUsageConverter::couponUsageAllToDto,
        CouponUsageMerchant.class, CouponUsageConverter::couponUsageMerchantToDto,
        CouponUsageStore.class, CouponUsageConverter::couponUsageStoreToDto
    );
    private static final String COUPON_USAGE_ALL_STRING = "모든 매장";

    private static String couponUsageAllToDto(CouponUsage couponUsageAll) {
        return COUPON_USAGE_ALL_STRING;
    }

    private static String couponUsageMerchantToDto(CouponUsage couponUsageMerchant) {
        return ((CouponUsageMerchant) couponUsageMerchant).getMerchant().getName();
    }

    private static String couponUsageStoreToDto(CouponUsage couponUsageStore) {
        return ((CouponUsageStore) couponUsageStore).getStore().getName();
    }

    public static String convert(CouponUsage couponUsage) {
        return COUPON_USAGE_MAPPER.get(couponUsage.getClass())
            .apply(couponUsage);
    }
}
