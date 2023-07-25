package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponUsageConverter;

/**
 * 쿠폰 데이터를 클라이언트에게 전달하는 dto.
 *
 * @author eora21
 * @since 2023.07.06
 */
@Getter
public class SelectOwnCouponResponseDto {
    private final UUID issueCouponCode;
    private final CouponTypeResponse couponTypeResponse;
    private final String couponUsageName;
    private final String name;
    private final String description;
    private final LocalDate expirationDate;
    private final String logTypeDescription;

    /**
     * Instantiates a new Select own coupon response dto.
     *
     * @param issueCouponCode    the issue coupon code
     * @param couponType         the coupon type
     * @param couponUsage        the coupon usage
     * @param name               the name
     * @param description        the description
     * @param expirationDate     the expiration date
     * @param logTypeDescription the log type description
     */
    @QueryProjection
    public SelectOwnCouponResponseDto(UUID issueCouponCode, CouponType couponType, CouponUsage couponUsage, String name,
                                      String description, LocalDate expirationDate, String logTypeDescription) {
        this.issueCouponCode = issueCouponCode;
        this.couponTypeResponse = CouponTypeConverter.convert(couponType);
        this.couponUsageName = CouponUsageConverter.convert(couponUsage);
        this.name = name;
        this.description = description;
        this.expirationDate = expirationDate;
        this.logTypeDescription = logTypeDescription;
    }
}
