package store.cookshoong.www.cookshoongbackend.rabbitmq.exception;

/**
 * lock 처리 도중 인터럽트가 발생했을 경우 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.29
 */
public class LockInterruptedException extends RuntimeException {
    public LockInterruptedException() {
        super("예상치 못 한 오류가 발생했습니다.");
    }
}
