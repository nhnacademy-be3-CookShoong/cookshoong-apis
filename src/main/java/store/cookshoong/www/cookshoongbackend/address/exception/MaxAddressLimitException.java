package store.cookshoong.www.cookshoongbackend.address.exception;

/**
 * {설명을 작성해주세요}.
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
