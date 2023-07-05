package store.cookshoong.www.cookshoongbackend.address.exception;

/**
 * 회원이 가지고 있는 주소가 10개 초과될 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class MaxAddressLimitException extends RuntimeException {

    private static final String MESSAGE = "회원이 가지고 있는 주소가 10개가 넘었습니다. 주소 삭제 바랍니다.";

    public MaxAddressLimitException() {
        super(MESSAGE);
    }
}
