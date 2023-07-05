package store.cookshoong.www.cookshoongbackend.address.exception;

/**
 * {설명을 작성해주세요}.
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
