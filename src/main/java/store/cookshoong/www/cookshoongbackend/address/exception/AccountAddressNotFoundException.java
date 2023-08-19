package store.cookshoong.www.cookshoongbackend.address.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 회원의 주소가 존재하지 않을 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class AccountAddressNotFoundException extends NotFoundException {

    private static final String MESSAGE = "주소";

    public AccountAddressNotFoundException() {
        super(MESSAGE);
    }
}
