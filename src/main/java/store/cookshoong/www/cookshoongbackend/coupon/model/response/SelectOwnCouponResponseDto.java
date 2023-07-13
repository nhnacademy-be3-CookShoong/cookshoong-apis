package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeResponse;

/**
 * 쿠폰 데이터를 클라이언트에게 전달하는 dto.
 *
 * @author eora21
 * @since 2023.07.06
 */
@AllArgsConstructor
public class SelectOwnCouponResponseDto {
    private UUID issueCouponCode;
    private CouponTypeResponse couponTypeResponse;
    private String couponUsageName;
    private String name;
    private String description;
    private LocalDateTime expirationAt;
    private String logTypeDescription;
}
