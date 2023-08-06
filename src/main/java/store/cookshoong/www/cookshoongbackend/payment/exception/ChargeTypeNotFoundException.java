package store.cookshoong.www.cookshoongbackend.payment.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 결제타입이 존재하지 않을 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
public class ChargeTypeNotFoundException extends NotFoundException {

    private static final String MESSAGE = "결제 타입";

    public ChargeTypeNotFoundException() {
        super(MESSAGE);
    }
}
