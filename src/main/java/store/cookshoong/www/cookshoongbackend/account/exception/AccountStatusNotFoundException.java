package store.cookshoong.www.cookshoongbackend.account.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 회원상태 변경시 없는 상태로 변경시 일어나는 예외.
 *
 * @author koesnam (추만석)
 * @since 2023.07.29
 */
public class AccountStatusNotFoundException extends NotFoundException {
    public AccountStatusNotFoundException(String code) {
        super("상태(" + code + ")");
    }
}
