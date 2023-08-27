package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 쿠폰 발급에 실패했을 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.22
 */
public class ProvideIssueCouponFailureException extends CustomCouponException {
    public ProvideIssueCouponFailureException() {
        super("쿠폰 발급 실패. 다시 시도해주세요.");
    }
}
