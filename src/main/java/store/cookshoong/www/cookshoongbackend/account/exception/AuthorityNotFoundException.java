package store.cookshoong.www.cookshoongbackend.account.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 회원 권한 요청시 발생하는 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class AuthorityNotFoundException extends NotFoundException {
    public AuthorityNotFoundException() {
        super("권한");
    }
}
