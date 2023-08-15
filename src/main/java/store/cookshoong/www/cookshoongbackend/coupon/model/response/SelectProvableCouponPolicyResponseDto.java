package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;

/**
 * 쿠폰 발급을 위해 정책 id와 쿠폰 타입, 사용 가능 기간을 전달하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.07.24
 */
@Getter
public class SelectProvableCouponPolicyResponseDto {
    private final Long couponPolicyId;
    private final CouponTypeResponse couponTypeResponse;
    private final Integer usagePeriod;

    /**
     * 쿠폰 타입과 기간은 쿠폰의 정보를 나타내기 위해, id는 발급을 위해 담는다.
     *
     * @param couponPolicyId the coupon policy id
     * @param couponType     the coupon type
     * @param usagePeriod    the usage period
     */
    @QueryProjection
    public SelectProvableCouponPolicyResponseDto(Long couponPolicyId, CouponType couponType, Integer usagePeriod) {
        this.couponPolicyId = couponPolicyId;
        this.couponTypeResponse = CouponTypeConverter.convert(couponType);
        this.usagePeriod = usagePeriod;
    }
}
