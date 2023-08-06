package store.cookshoong.www.cookshoongbackend.order.advice;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * OrderController 예외를 처리하는 Advice.
 *
 * @author eora21 (김주호)
 * @since 2023.08.05
 */
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.order.controller")
public class OrderControllerAdvice {
    @ExceptionHandler(ValidationFailureException.class)
    ResponseEntity<Map<String, String>> handleValidationFailure(ValidationFailureException e) {
        return ResponseEntity.badRequest()
            .body(e.getErrors());
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<String> handleIssueCouponRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
    }
}
