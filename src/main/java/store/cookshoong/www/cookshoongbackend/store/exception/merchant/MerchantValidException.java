package store.cookshoong.www.cookshoongbackend.store.exception.merchant;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 가맹점 Request 에 대한 valid 예외.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class MerchantValidException extends ValidationFailureException {
    public MerchantValidException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
