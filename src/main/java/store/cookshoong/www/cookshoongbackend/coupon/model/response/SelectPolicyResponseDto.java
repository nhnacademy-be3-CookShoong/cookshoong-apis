package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;

/**
 * 쿠폰 정책 데이터를 클라이언트에게 전달하는 dto.
 *
 * @author eora21(김주호)
 * @since 2023.07.14
 */
@Getter
public class SelectPolicyResponseDto {
    private final Long id;
    private final CouponTypeResponse couponTypeResponse;
    private final String name;
    private final String description;
    private final Integer usagePeriod;
    private final Long unclaimedCouponCount;
    private final Long issueCouponCount;

    /**
     * Instantiates a new Select policy response dto.
     *
     * @param id                   the id
     * @param couponType           the coupon type
     * @param name                 the name
     * @param description          the description
     * @param usagePeriod          the usage period
     * @param unclaimedCouponCount the unclaimed coupon count
     * @param issueCouponCount     the issue coupon count
     */
    @QueryProjection
    public SelectPolicyResponseDto(Long id, CouponType couponType, String name, String description, Integer usagePeriod,
                                   Long unclaimedCouponCount, Long issueCouponCount) {
        this.id = id;
        this.couponTypeResponse = CouponTypeConverter.convert(couponType);
        this.name = name;
        this.description = description;
        this.usagePeriod = usagePeriod;
        this.unclaimedCouponCount = unclaimedCouponCount;
        this.issueCouponCount = issueCouponCount;
    }
}
