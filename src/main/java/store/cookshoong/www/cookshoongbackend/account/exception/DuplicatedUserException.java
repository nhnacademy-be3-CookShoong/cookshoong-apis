package store.cookshoong.www.cookshoongbackend.account.exception;

/**
 * 회원아이디 중복시 발생하는 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class DuplicatedUserException extends RuntimeException {
    public DuplicatedUserException(String loginId) {
        super(loginId + "는 이미 존재하는 아이디입니다.");
    }
}
