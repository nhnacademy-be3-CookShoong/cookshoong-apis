package store.cookshoong.www.cookshoongbackend.review.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 회원의 주소가 존재하지 않을 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class ReviewReplyNotFoundException extends NotFoundException {

    private static final String MESSAGE = "리뷰 답글";

    public ReviewReplyNotFoundException() {
        super(MESSAGE);
    }
}
