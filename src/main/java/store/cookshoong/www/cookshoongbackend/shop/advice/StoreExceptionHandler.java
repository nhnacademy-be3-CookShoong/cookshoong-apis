package store.cookshoong.www.cookshoongbackend.shop.advice;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.DuplicatedBankException;
import store.cookshoong.www.cookshoongbackend.shop.exception.businesshour.BusinessHourValidationException;
import store.cookshoong.www.cookshoongbackend.shop.exception.businesshour.DayTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.DuplicatedStoreCategoryException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryValidException;
import store.cookshoong.www.cookshoongbackend.shop.exception.holiday.HolidayValidationException;
import store.cookshoong.www.cookshoongbackend.shop.exception.holiday.HolidayNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.DuplicatedMerchantException;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.MerchantNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.MerchantValidException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreValidException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.UserAccessDeniedException;

/**
 * shop 패키지 아래에서 RestController 내에서 일어나는 모든 예외들을 처리한다.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.shop")
public class StoreExceptionHandler {
    /**
     * 존재하지 않는 것에 대한 예외처리.
     *
     * @param e 없을 때 예외
     * @return 404
     */
    @ExceptionHandler(value = {StoreNotFoundException.class, BankTypeNotFoundException.class,
        StoreCategoryNotFoundException.class, MerchantNotFoundException.class, HolidayNotFoundException.class,
        StoreStatusNotFoundException.class, DayTypeNotFoundException.class})
    public ResponseEntity<String> somethingNotFoundError(NotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(e.getMessage());
    }

    /**
     * valid 에러에 대한 예외처리.
     *
     * @param e valid 예외
     * @return 400
     */
    @ExceptionHandler(value = {StoreValidException.class, MerchantValidException.class,
        StoreCategoryValidException.class, BusinessHourValidationException.class,
        HolidayValidationException.class})
    public ResponseEntity<Map<String, String>> validFailure(ValidationFailureException e) {
        return ResponseEntity
            .badRequest()
            .body(e.getErrors());
    }

    /**
     * 이미 존재하는 것에 대한 예외처리.
     *
     * @param e 중복에 대한 예외
     * @return 409
     */
    @ExceptionHandler(value = {DuplicatedBusinessLicenseException.class, DuplicatedMerchantException.class,
        DuplicatedStoreCategoryException.class, DuplicatedBankException.class})
    public ResponseEntity<String> alreadySomethingExistsError(Exception e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(e.getMessage());
    }

    /**
     * 매장 접근권한이 있는 사용자 외 접근 거부에 대한 예외처리.
     *
     * @param e the 접근 거부에 대한 에외처리
     * @return 403
     */
    @ExceptionHandler(value = UserAccessDeniedException.class)
    public ResponseEntity<String> userAccessDeniedError(Exception e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
    }
}
