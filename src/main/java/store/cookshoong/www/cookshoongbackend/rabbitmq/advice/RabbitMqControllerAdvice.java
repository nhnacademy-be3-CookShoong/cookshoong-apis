package store.cookshoong.www.cookshoongbackend.rabbitmq.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockInterruptedException;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockOverWaitTimeException;

/**
 * RabbitMqController 예외를 처리하는 Advice.
 *
 * @author eora21 (김주호)
 * @since 2023.07.29
 */
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.rabbitmq.controller")
public class RabbitMqControllerAdvice {

    @ExceptionHandler({
        LockInterruptedException.class,
        LockOverWaitTimeException.class
    })
    ResponseEntity<String> handleLockException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
    }
}
