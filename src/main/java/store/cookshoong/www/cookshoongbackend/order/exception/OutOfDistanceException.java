package store.cookshoong.www.cookshoongbackend.order.exception;

/**
 * 거리 검증이 실패했을 경우 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.04
 */
public class OutOfDistanceException extends RuntimeException {
    public OutOfDistanceException() {
        super("주문 가능한 거리를 벗어났습니다.");
    }
}
