package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 사용된 쿠폰 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.03
 */
public class AlreadyUsedCouponException extends CustomCouponException {
    public AlreadyUsedCouponException() {
        super("이미 사용된 쿠폰입니다.");
    }
}
