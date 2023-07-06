package store.cookshoong.www.cookshoongbackend.account.advice;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * account 패키지 아래에서 RestController 내에서 일어나는 모든 예외들을 처리한다.
 *
 * @author koesnam
 * @since 2023.07.05
 */
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.account")
public class RequestExceptionHandler {
    // TODO: 에러메세지로 무엇을 보내줄 지
    @ExceptionHandler({DuplicatedUserException.class})
    public ResponseEntity<Void> handleConflictStatus(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .build();
    }

    @ExceptionHandler({ValidationFailureException.class})
    public ResponseEntity<Map<String, String>> handleValidationFailure(ValidationFailureException e) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
            .body(e.getErrors());
    }
}
