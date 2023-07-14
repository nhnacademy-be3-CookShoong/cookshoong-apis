package store.cookshoong.www.cookshoongbackend.coupon.model.temp;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;

/**
 * 사용자의 쿠폰 정보를 임시로 담을 dto.
 * CouponType, CouponUsage entity 객체를 그대로 들고 있으며, 실제 응답 객체로 변경될 때 dto 전환이 이루어진다.
 *
 * @author eora21
 * @since 2023.07.06
 */
@Getter
public class SelectOwnCouponResponseTempDto {
    private final UUID issueCouponCode;
    private final CouponType couponType;
    private final CouponUsage couponUsage;
    private final String name;
    private final String description;
    private final LocalDateTime expirationAt;
    private final String logTypeDescription;

    /**
     * 쿠폰 임시 응답 객체 생성자.
     *
     * @param issueCouponCode    쿠폰 식별번호
     * @param couponType         쿠폰 타입
     * @param couponUsage        쿠폰 사용처
     * @param name               쿠폰 이름
     * @param description        쿠폰 설명
     * @param expirationAt       쿠폰 만료일
     * @param logTypeDescription 쿠폰 기록 정보
     */
    @QueryProjection
    public SelectOwnCouponResponseTempDto(UUID issueCouponCode, CouponType couponType, CouponUsage couponUsage,
                                          String name, String description, LocalDateTime expirationAt,
                                          String logTypeDescription) {
        this.issueCouponCode = issueCouponCode;
        this.couponType = couponType;
        this.couponUsage = couponUsage;
        this.name = name;
        this.description = description;
        this.expirationAt = expirationAt;
        this.logTypeDescription = logTypeDescription;
    }
}
