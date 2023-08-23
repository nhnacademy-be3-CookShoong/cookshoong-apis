package store.cookshoong.www.cookshoongbackend.payment.advice;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeValidationException;
import store.cookshoong.www.cookshoongbackend.payment.exception.RefundTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.exception.RefundValidationException;
import store.cookshoong.www.cookshoongbackend.payment.exception.TypeValidationException;

/**
 * payment 패키지 아래에서 RestController 내에서 일어나는 모든 예외들을 처리한다.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.payment")
public class PaymentRequestExceptionHandler {

    @ExceptionHandler({TypeValidationException.class, ChargeValidationException.class, RefundValidationException.class})
    public ResponseEntity<Map<String, String>> handleValidationFailure(ValidationFailureException e) {

        return ResponseEntity.badRequest().body(e.getErrors());
    }

    @ExceptionHandler({ChargeTypeNotFoundException.class, RefundTypeNotFoundException.class})
    public ResponseEntity<String> chargeTypeNotFoundException(NotFoundException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
