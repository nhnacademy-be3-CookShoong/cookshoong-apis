package store.cookshoong.www.cookshoongbackend.point.exception;

/**
 * 소유한 포인트가 사용할 포인트보다 적을 시 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
public class LowPointException extends RuntimeException {
    public LowPointException() {
        super("포인트가 부족합니다.");
    }
}
