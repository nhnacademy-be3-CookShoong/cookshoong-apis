package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 쿠폰 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.27
 */
public class CustomCouponException extends RuntimeException {
    public CustomCouponException(String message) {
        super(message);
    }
}
