package store.cookshoong.www.cookshoongbackend.address.exception;

/**
 * 회원의 주소가 존재하지 않을 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class AccountAddressNotFoundException extends RuntimeException {

    private static final String MESSAGE = "회원의 주소가 존재하지 않습니다.";

    public AccountAddressNotFoundException() {
        super(MESSAGE);
    }
}
