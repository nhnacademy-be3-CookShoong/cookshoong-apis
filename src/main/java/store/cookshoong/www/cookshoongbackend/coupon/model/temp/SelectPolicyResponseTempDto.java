package store.cookshoong.www.cookshoongbackend.coupon.model.temp;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;

/**
 * 쿠폰 정책을 임시로 담을 dto.
 * CouponType entity 객체를 그대로 들고 있으며, 실제 응답 객체로 변경될 때 dto 전환이 이루어진다.
 *
 * @author eora21(김주호)
 * @since 2023.07.14
 */
@Getter
public class SelectPolicyResponseTempDto {
    private final Long id;
    private final CouponType couponType;
    private final String name;
    private final String description;
    private final Integer usagePeriod;
    private final Long unclaimedCouponCount;
    private final Long issueCouponCount;

    /**
     * 쿠폰 정책 임시 응답 객체 생성자.
     *
     * @param id                   쿠폰 정책 id
     * @param couponType           쿠폰 타입
     * @param name                 쿠폰 이름
     * @param description          쿠폰 설명
     * @param usagePeriod          쿠폰 만료시간
     * @param unclaimedCouponCount 발급되었으나 유저가 지니고 있지 않은 쿠폰 개수
     * @param issueCouponCount     발급된 전체 쿠폰 개수
     */
    @QueryProjection
    public SelectPolicyResponseTempDto(Long id, CouponType couponType, String name, String description,
                                       Integer usagePeriod, Long unclaimedCouponCount, Long issueCouponCount) {
        this.id = id;
        this.couponType = couponType;
        this.name = name;
        this.description = description;
        this.usagePeriod = usagePeriod;
        this.unclaimedCouponCount = unclaimedCouponCount;
        this.issueCouponCount = issueCouponCount;
    }
}
