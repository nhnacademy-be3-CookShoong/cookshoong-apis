package store.cookshoong.www.cookshoongbackend.address.address;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.address.exception.AccountAddressNotFoundException;
import store.cookshoong.www.cookshoongbackend.address.exception.CreateAccountAddressValidationException;
import store.cookshoong.www.cookshoongbackend.address.exception.MaxAddressLimitException;
import store.cookshoong.www.cookshoongbackend.address.exception.ModifyAccountAddressValidationException;
import store.cookshoong.www.cookshoongbackend.common.exception.ErrorMessage;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 주소에 대한 Exception Handler.
 *
 * @author jeongjewan
 * @since 2023.07.12
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.address")
public class AddressRequestExceptionHandler {


    /**
     * (400) 검증 실패에 대한 응답.
     *
     * @param e             검증 실패 예외
     * @return              검증 실패 필드와 에러 메시지 반환
     */
    @ExceptionHandler({CreateAccountAddressValidationException.class, ModifyAccountAddressValidationException.class})
    public ResponseEntity<Map<String, String>> handleValidationFailure(ValidationFailureException e) {

        return ResponseEntity.badRequest().body(e.getErrors());
    }

    /**
     * (404) 객체 조회 실패에 대한 예외.
     *
     * @param e             조회실패 예외
     * @return              조회 실패 필드와 에러 메시지 반환
     */
    @ExceptionHandler({AccountAddressNotFoundException.class})
    public ResponseEntity<ErrorMessage> accountAddressNotFoundException(NotFoundException e) {


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e));
    }

    /**
     * (400) 주소 등록 10개 초과로 인한 예외.
     *
     * @param e             주소 10개 초과 예외.
     * @return              주소 10개 초과 에러 메시지 반환
     */
    @ExceptionHandler({MaxAddressLimitException.class})
    public ResponseEntity<ErrorMessage> maxAddressLimitException(MaxAddressLimitException e) {


        return ResponseEntity.badRequest().body(new ErrorMessage(e));
    }
}
