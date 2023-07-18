package store.cookshoong.www.cookshoongbackend.menu_order.advice;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuGroupValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionValidationException;

/**
 * menu 패키지 아래 RestController 내에서 일어나는 모든 예외 처리.
 *
 * @author papel
 * @since 2023.07.13
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.menu")
public class MenuExceptionHandler {
    /**
     * 존재하지 않는 것에 대한 예외처리.
     *
     * @param e 없을 때 예외
     * @return 404
     */
    @ExceptionHandler(value = {MenuStatusNotFoundException.class, OptionGroupNotFoundException.class})
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
    @ExceptionHandler(value = {MenuGroupValidationException.class, MenuValidationException.class,
        OptionGroupValidationException.class, OptionValidationException.class})
    public ResponseEntity<Map<String, String>> validFailure(ValidationFailureException e) {
        return ResponseEntity
            .badRequest()
            .body(e.getErrors());
    }
}
