package store.cookshoong.www.cookshoongbackend.common.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.BindingResult;

/**
 * 입력값에 대한 공통적인 검증실패 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public abstract class ValidationFailureException extends RuntimeException {
    private final Map<String, String> errors;

    protected ValidationFailureException(BindingResult bindingResult) {
        errors = new HashMap<>();

        bindingResult.getFieldErrors()
            .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
