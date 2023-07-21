package store.cookshoong.www.cookshoongbackend.coupon.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 쿠폰 발행 요청 데이터의 위반사항 발견 시 발생되는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.21
 */
public class IssueCouponRequestValidationException extends ValidationFailureException {
    public IssueCouponRequestValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
