package store.cookshoong.www.cookshoongbackend.menu_order.exception.option;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 옵션 그룹 생성 요청 데이터의 위반사항 발견 예외.
 *
 * @author papel (윤동현)
 * @since 2023.07.13
 */
public class OptionGroupValidationException extends ValidationFailureException {
    public OptionGroupValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

