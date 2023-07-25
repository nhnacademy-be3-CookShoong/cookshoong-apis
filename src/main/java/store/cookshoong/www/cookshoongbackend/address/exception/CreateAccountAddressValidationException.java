package store.cookshoong.www.cookshoongbackend.address.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 회원의 주소에 대한 Valid Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class CreateAccountAddressValidationException extends ValidationFailureException {
    public CreateAccountAddressValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

