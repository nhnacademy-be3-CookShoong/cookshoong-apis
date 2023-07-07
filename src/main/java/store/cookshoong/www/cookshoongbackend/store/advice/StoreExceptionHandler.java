package store.cookshoong.www.cookshoongbackend.store.advice;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.store.exception.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.store.exception.SelectStoreNotFoundException;

/**
 * store 패키지 아래에서 RestController 내에서 일어나는 모든 예외들을 처리한다.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.store")
public class StoreExceptionHandler {
    /**
     * 이미 존재하는 것에 대한 예외처리.
     *
     * @param e the e
     * @return the response entity
     */
    @ExceptionHandler(value = {SelectStoreNotFoundException.class, BankTypeNotFoundException.class})
    public ResponseEntity<String> somethingNotFoundError(NotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(e.getMessage());
    }

    /**
     * valid 에러에 대한 예외처리.
     *
     * @param e the e
     * @return the response entity
     */
    @ExceptionHandler(value = {ValidationFailureException.class})
    public ResponseEntity<Map<String, String>> validFailure(ValidationFailureException e) {
        return ResponseEntity
            .badRequest()
            .body(e.getErrors());
    }

    /**
     * 이미 존재하는 것에 대한 예외처리.
     *
     * @param e Exception
     * @return 에러 처리 결과메시
     */
    @ExceptionHandler(value = {DuplicatedBusinessLicenseException.class})
    public ResponseEntity<String> alreadySomethingExistsError(Exception e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(e.getMessage());
    }
}
