package store.cookshoong.www.cookshoongbackend.rabbitmq.exception;

/**
 * lock 대기 시간이 초과되었을 경우 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.29
 */
public class LockOverWaitTimeException extends RuntimeException {
    public LockOverWaitTimeException() {
        super("시간이 초과되었습니다.");
    }
}
