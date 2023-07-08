package store.cookshoong.www.cookshoongbackend.store.exception.category;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 매장 카테고리 Valid 예외.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
public class StoreCategoryValidException extends ValidationFailureException {
    public StoreCategoryValidException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
