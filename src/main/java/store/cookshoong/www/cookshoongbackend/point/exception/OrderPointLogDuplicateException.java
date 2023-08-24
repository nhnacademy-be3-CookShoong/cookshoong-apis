package store.cookshoong.www.cookshoongbackend.point.exception;

/**
 * 하나의 주문에 대한 포인트 적립이 2개 이상일 시 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
public class OrderPointLogDuplicateException extends RuntimeException {
    public OrderPointLogDuplicateException() {
        super("해당 주문으로 사용한 포인트가 정상 검색되지 않았습니다. 관리자에게 문의해주세요.");
    }
}
