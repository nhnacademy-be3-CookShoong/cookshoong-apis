package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;

/**
 * 매장에서 쿠폰 발급을 위해 정책 id와 쿠폰 타입, 사용 가능 기간을 전달하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.07.24
 */
@Getter
public class SelectProvableStoreCouponPolicyResponseDto {
    private final Long couponPolicyId;
    private final CouponTypeResponse couponTypeResponse;
    private final Integer usagePeriod;

    /**
     * Instantiates a new Select provable store coupon policy response dto.
     *
     * @param couponPolicyId the coupon policy id
     * @param couponType     the coupon type
     * @param usagePeriod    the usage period
     */
    @QueryProjection
    public SelectProvableStoreCouponPolicyResponseDto(Long couponPolicyId, CouponType couponType, Integer usagePeriod) {
        this.couponPolicyId = couponPolicyId;
        this.couponTypeResponse = CouponTypeConverter.convert(couponType);
        this.usagePeriod = usagePeriod;
    }
}
