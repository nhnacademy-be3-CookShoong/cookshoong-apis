package store.cookshoong.www.cookshoongbackend.order.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 주문 요청 데이터의 위반사항 발견 시 발생되는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.05
 */
public class OrderRequestValidationException extends ValidationFailureException {
    public OrderRequestValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
