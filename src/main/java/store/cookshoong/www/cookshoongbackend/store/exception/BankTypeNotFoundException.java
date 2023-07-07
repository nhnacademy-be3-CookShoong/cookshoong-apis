package store.cookshoong.www.cookshoongbackend.store.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 은행 코드 요청에 대한 예외발생.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
public class BankTypeNotFoundException extends NotFoundException {
    public BankTypeNotFoundException(String bankTypeCode) {
        super(bankTypeCode + "는 없는 은행 코드입니다.");

    }
}
