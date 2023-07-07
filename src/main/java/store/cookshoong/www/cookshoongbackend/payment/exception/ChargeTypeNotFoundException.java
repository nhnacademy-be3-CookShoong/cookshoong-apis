package store.cookshoong.www.cookshoongbackend.payment.exception;

/**
 * 회원의 주소가 존재하지 않을 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class ChargeTypeNotFoundException extends RuntimeException {

    private static final String MESSAGE = "결제 타입이 존재하지 않습니다.";

    public ChargeTypeNotFoundException() {
        super(MESSAGE);
    }
}
