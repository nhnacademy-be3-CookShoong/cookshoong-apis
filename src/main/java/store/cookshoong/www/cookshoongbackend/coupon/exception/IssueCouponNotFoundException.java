package store.cookshoong.www.cookshoongbackend.coupon.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 발행 쿠폰을 찾을 수 없을 때 발생되는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.22
 */
public class IssueCouponNotFoundException extends NotFoundException {
    public IssueCouponNotFoundException() {
        super("발행 쿠폰");
    }
}
