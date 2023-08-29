package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 쿠폰이 모두 소진되어 발급받을 수 없을 시 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.29
 */
public class CouponExhaustionException extends CustomCouponException {
    public CouponExhaustionException() {
        super("쿠폰이 모두 소진되었습니다.");
    }
}
