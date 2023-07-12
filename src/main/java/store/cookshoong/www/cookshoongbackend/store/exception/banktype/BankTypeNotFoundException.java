package store.cookshoong.www.cookshoongbackend.store.exception.banktype;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 은행 코드 요청에 대한 예외발생.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class BankTypeNotFoundException extends NotFoundException {
    public BankTypeNotFoundException() {
        super("해당 은행은 서비스 제공되지 않습니다.");

    }
}
