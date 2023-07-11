package store.cookshoong.www.cookshoongbackend.store.advice;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.store.exception.HolidayValidationException;
import store.cookshoong.www.cookshoongbackend.store.exception.SelectHolidayNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.banktype.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.banktype.DuplicatedBankException;
import store.cookshoong.www.cookshoongbackend.store.exception.category.DuplicatedStoreCategoryException;
import store.cookshoong.www.cookshoongbackend.store.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.category.StoreCategoryValidException;
import store.cookshoong.www.cookshoongbackend.store.exception.merchant.DuplicatedMerchantException;
import store.cookshoong.www.cookshoongbackend.store.exception.merchant.MerchantNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.merchant.MerchantValidException;
import store.cookshoong.www.cookshoongbackend.store.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.store.exception.store.SelectStoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.store.StoreValidException;

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
     * @param e 없을 때 예외
     * @return 예외처리 메시지 반환
     */
    @ExceptionHandler(value = {SelectStoreNotFoundException.class, BankTypeNotFoundException.class,
        StoreCategoryNotFoundException.class, MerchantNotFoundException.class, SelectHolidayNotFoundException.class})
    public ResponseEntity<String> somethingNotFoundError(NotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(e.getMessage());
    }

    /**
     * valid 에러에 대한 예외처리.
     *
     * @param e valid 예외
     * @return 예외처리 메시지 반환
     */
    @ExceptionHandler(value = {StoreValidException.class, MerchantValidException.class,
        StoreCategoryValidException.class, HolidayValidationException.class})
    public ResponseEntity<Map<String, String>> validFailure(ValidationFailureException e) {
        return ResponseEntity
            .badRequest()
            .body(e.getErrors());
    }

    /**
     * 이미 존재하는 것에 대한 예외처리.
     *
     * @param e 중복에 대한 예외
     * @return 에러 처리 메시지 반환
     */
    @ExceptionHandler(value = {DuplicatedBusinessLicenseException.class, DuplicatedMerchantException.class,
        DuplicatedStoreCategoryException.class, DuplicatedBankException.class})
    public ResponseEntity<String> alreadySomethingExistsError(Exception e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(e.getMessage());
    }
}
