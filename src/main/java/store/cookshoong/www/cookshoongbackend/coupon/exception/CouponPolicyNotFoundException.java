package store.cookshoong.www.cookshoongbackend.coupon.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 쿠폰 정책을 찾을 수 없을 때 발생되는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.19
 */
public class CouponPolicyNotFoundException extends NotFoundException {
    public CouponPolicyNotFoundException() {
        super("쿠폰 정책");
    }
}
