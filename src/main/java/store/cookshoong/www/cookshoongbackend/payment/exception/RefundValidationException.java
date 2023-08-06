package store.cookshoong.www.cookshoongbackend.payment.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 환불 타입에 대한 Valid Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class RefundValidationException extends ValidationFailureException {
    public RefundValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

