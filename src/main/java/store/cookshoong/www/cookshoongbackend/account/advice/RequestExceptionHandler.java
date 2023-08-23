package store.cookshoong.www.cookshoongbackend.account.advice;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.account.exception.AccountStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.AuthorityNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.exception.SignUpValidationException;
import store.cookshoong.www.cookshoongbackend.account.exception.UpdateAccountInfoValidationException;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ErrorMessage;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * account 패키지 아래에서 RestController 내에서 일어나는 모든 예외들을 처리한다.
 *
 * @author koesnam
 * @since 2023.07.05
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.account")
public class RequestExceptionHandler {

    /**
     * (400) 검증 실패에 대한 응답.
     *
     * @param e 검증실패 예외
     * @return 검증실패 필드와 메세지
     */
    @ExceptionHandler({SignUpValidationException.class, UpdateAccountInfoValidationException.class})
    public ResponseEntity<Map<String, String>> handleValidationFailure(ValidationFailureException e,
                                                                       HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(e.getErrors());
    }

    /**
     * (404) 객체 조회 실패에 대한 예외.
     *
     * @param e 조회실패 예외
     * @return 조회실패 필드와 메세지
     */
    @ExceptionHandler({AuthorityNotFoundException.class, UserNotFoundException.class,
        AccountStatusNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException e,
                                                                HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorMessage(e));
    }

    /**
     * (409) 서버의 상태와 충돌시에 대한 응답(중복값 충돌, ...).
     *
     * @param e 예외
     * @return 예외메세지
     */
    @ExceptionHandler({DuplicatedUserException.class})
    public ResponseEntity<ErrorMessage> handleConflictStatus(Exception e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorMessage(e));
    }
}
