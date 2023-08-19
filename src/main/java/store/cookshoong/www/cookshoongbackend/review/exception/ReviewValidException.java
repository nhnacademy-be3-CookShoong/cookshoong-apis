package store.cookshoong.www.cookshoongbackend.review.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * 리뷰 등록 및 수정 시 valid 체크.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
public class ReviewValidException extends ValidationFailureException {
    public ReviewValidException(BindingResult bindingResult) {
        super(bindingResult);
    }
}
