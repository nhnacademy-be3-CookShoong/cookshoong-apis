package store.cookshoong.www.cookshoongbackend.coupon.advice;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CustomCouponException;

/**
 * CouponController 예외를 처리하는 Advice.
 *
 * @author eora21(김주호)
 * @since 2023.07.13
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.coupon.controller")
public class CouponControllerAdvice {

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

    @ExceptionHandler(CustomCouponException.class)
    ResponseEntity<String> handleCustomCouponException(CustomCouponException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    ResponseEntity<String> handleThrowable(Throwable e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("예상치 못 한 오류가 발생했습니다.");
    }
}
