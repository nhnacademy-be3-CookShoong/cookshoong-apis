package store.cookshoong.www.cookshoongbackend.order.exception;

/**
 * 장바구니에 담겼던 가격보다 현재 가격이 비싸졌을 경우 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.03
 */
public class PriceIncreaseException extends RuntimeException {
    public PriceIncreaseException() {
        super("특정 품목의 가격이 상승하여 주문할 수 없습니다. 재주문 해주세요.");
    }
}
