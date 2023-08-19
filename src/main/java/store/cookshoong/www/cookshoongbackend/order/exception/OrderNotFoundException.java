package store.cookshoong.www.cookshoongbackend.order.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 주문 상태를 찾지 못 했을 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.02
 */
public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException() {
        super("주문");
    }
}
