package store.cookshoong.www.cookshoongbackend.store.exception.store;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 매장 Request 에 대한 valid 예외.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class StoreValidException extends ValidationFailureException {
    public StoreValidException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
