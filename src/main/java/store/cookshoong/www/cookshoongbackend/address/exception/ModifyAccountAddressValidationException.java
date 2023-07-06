package store.cookshoong.www.cookshoongbackend.address.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 회원가입 요청 데이터의 위반사항 발견 발생되는 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class ModifyAccountAddressValidationException extends ValidationFailureException {
    public ModifyAccountAddressValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

