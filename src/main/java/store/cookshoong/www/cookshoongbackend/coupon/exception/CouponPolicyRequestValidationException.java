package store.cookshoong.www.cookshoongbackend.coupon.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 쿠폰 정책 생성 요청 데이터의 위반사항 발견 시 발생되는 예외.
 *
 * @author eora21
 * @since 2023.07.08
 */
public class CouponPolicyRequestValidationException extends ValidationFailureException {
    public CouponPolicyRequestValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
