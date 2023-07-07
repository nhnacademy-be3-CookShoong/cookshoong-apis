package store.cookshoong.www.cookshoongbackend.coupon.model.request;

import java.time.LocalTime;

/**
 * 쿠폰 정책 생성 시 필요한 메서드를 정의한 인터페이스.
 *
 * @author eora21
 * @since 2023.07.05
 */
public interface CouponPolicyRequest {
    String getName();

    String getDescription();

    LocalTime getExpirationTime();
}
