package store.cookshoong.www.cookshoongbackend.account.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;

/**
 * Account 가 가지는 Controller Exception 들을 처리해줄 Advice.
 *
 * @author koesnam
 * @since 2023.07.05
 */
@RestControllerAdvice
public class ControllerAdvice {
    // TODO: 에러메세지로 무엇을 보내줄 지
    @ExceptionHandler({DuplicatedUserException.class})
    public ResponseEntity<Void> handleConflictStatus(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .build();
    }
}
