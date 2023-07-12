package store.cookshoong.www.cookshoongbackend.store.exception.businesshour;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 영업시간 생성 요청 데이터의 위반사항 발견 발생되는 예외.
 *
 * @author papel
 * @since 2023.07.10
 */
public class BusinessHourValidationException extends ValidationFailureException {
    public BusinessHourValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

