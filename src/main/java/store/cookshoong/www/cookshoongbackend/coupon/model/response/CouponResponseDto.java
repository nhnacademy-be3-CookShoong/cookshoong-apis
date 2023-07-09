package store.cookshoong.www.cookshoongbackend.coupon.model.response;

import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;

/**
 * 쿠폰 데이터를 클라이언트에게 전달하는 dto.
 *
 * @author eora21
 * @since 2023.07.06
 */
@AllArgsConstructor
public class CouponResponseDto {
    private UUID issueCouponCode;
    private CouponTypeResponse couponTypeResponse;
    private String couponUsageName;
    private String name;
    private String description;
    private LocalTime expirationAt;
}
