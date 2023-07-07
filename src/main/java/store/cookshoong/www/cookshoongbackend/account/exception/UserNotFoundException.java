package store.cookshoong.www.cookshoongbackend.account.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 사용자가 존재하지 않을 때 예외.
 * AccountNotFoundException이 javax.security.auth.login에 이미 존재하기 때문에 UserNotFoundException 으로 작성함.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super("존재하지 않는 회원입니다.");
    }
}
