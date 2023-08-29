package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 정상 발급되지 않은 쿠폰 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.03
 */
public class NonIssuedCouponProperlyException extends CustomCouponException {
    public NonIssuedCouponProperlyException() {
        super("정상 발급되지 않은 쿠폰입니다.");
    }
}
