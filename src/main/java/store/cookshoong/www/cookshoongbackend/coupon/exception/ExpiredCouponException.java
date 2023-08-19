package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 만료된 쿠폰 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.03
 */
public class ExpiredCouponException extends RuntimeException {
    public ExpiredCouponException() {
        super("만료된 쿠폰입니다.");
    }
}
