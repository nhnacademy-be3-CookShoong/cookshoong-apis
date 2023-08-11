package store.cookshoong.www.cookshoongbackend.coupon.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 발행된 쿠폰을 찾지 못 했을 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.01
 */
public class IssueCouponNotFoundException extends NotFoundException {
    public IssueCouponNotFoundException() {
        super("발행 쿠폰");
    }
}
