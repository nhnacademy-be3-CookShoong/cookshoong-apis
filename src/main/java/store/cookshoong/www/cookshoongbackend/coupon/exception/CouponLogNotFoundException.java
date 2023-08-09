package store.cookshoong.www.cookshoongbackend.coupon.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 쿠폰 로그를 찾을 수 없을 시 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
public class CouponLogNotFoundException extends NotFoundException {
    public CouponLogNotFoundException() {
        super("쿠폰 로그");
    }
}
