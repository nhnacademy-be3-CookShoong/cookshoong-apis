package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 이미 쿠폰을 발급받은 후 기간 내에 다시 쿠폰 발급을 요청할 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.23
 */
public class AlreadyHasCouponWithinSamePolicyException extends CustomCouponException {
    public AlreadyHasCouponWithinSamePolicyException() {
        super("이미 해당 쿠폰을 수령하셨습니다.");
    }
}
