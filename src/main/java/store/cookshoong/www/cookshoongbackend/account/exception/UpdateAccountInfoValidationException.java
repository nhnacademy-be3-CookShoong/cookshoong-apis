package store.cookshoong.www.cookshoongbackend.account.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 회원정보 수정 중 발생하는 예외.
 *
 * @author koesnam (추만석)
 * @since 2023.08.07
 */
public class UpdateAccountInfoValidationException extends ValidationFailureException {
    public UpdateAccountInfoValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
