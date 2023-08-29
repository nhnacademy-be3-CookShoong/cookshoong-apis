package store.cookshoong.www.cookshoongbackend.common.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.BindingResult;

/**
 * 입력값에 대한 공통적인 검증실패 예외.
 *
 * @author koesnam (추만석)
 * @since 2023.07.05
 */
public abstract class ValidationFailureException extends RuntimeException {
    private final Map<String, String> errors;


    /**
     * 전달받은 BindingResult 에서 필드에러들을 뽑아내서 저장하는 객체.
     *
     * @param bindingResult 에러들을 담고 있는 객체
     */
    protected ValidationFailureException(BindingResult bindingResult) {
        errors = new HashMap<>();

        bindingResult.getFieldErrors()
            .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return getErrors().toString();
    }
}
