package store.cookshoong.www.cookshoongbackend.store.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 휴업일 생성 요청 데이터의 위반사항 발견 발생되는 예외.
 *
 * @author papel
 * @since 2023.07.07
 */
public class CreateHolidayValidationException extends ValidationFailureException {
    public CreateHolidayValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

