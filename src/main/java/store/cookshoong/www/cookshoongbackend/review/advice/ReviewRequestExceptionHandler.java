package store.cookshoong.www.cookshoongbackend.review.advice;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.cookshoong.www.cookshoongbackend.common.exception.ErrorMessage;
import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewReplyNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewReplyValidException;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewValidException;

/**
 * 리뷰에 대한 Exception Handler.
 *
 * @author jeongjewan
 * @since 2023.08.15
 */
@Slf4j
@RestControllerAdvice(basePackages = "store.cookshoong.www.cookshoongbackend.review")
public class ReviewRequestExceptionHandler {


    /**
     * (400) 검증 실패에 대한 응답.
     *
     * @param e             검증 실패 예외
     * @param request       요청 Client Ip
     * @return              검증 실패 필드와 에러 메시지 반환
     */
    @ExceptionHandler({ReviewValidException.class, ReviewReplyValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationFailure(ValidationFailureException e,
                                                                       HttpServletRequest request) {

        return ResponseEntity.badRequest().body(e.getErrors());
    }

    /**
     * (404) 객체 조회 실패에 대한 예외.
     *
     * @param e             조회실패 예외
     * @param request       요청 Client Ip
     * @return              조회 실패 필드와 에러 메시지 반환
     */
    @ExceptionHandler({ReviewNotFoundException.class, ReviewReplyNotFoundException.class})
    public ResponseEntity<ErrorMessage> accountAddressNotFoundException(NotFoundException e,
                                                                        HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e));
    }
}
