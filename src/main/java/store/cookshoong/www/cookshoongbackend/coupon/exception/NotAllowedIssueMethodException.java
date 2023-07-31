package store.cookshoong.www.cookshoongbackend.coupon.exception;

/**
 * 허용되지 않는 발행 방법을 선택했을 시 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.07.26
 */
public class NotAllowedIssueMethodException extends RuntimeException {
    public NotAllowedIssueMethodException() {
        super("허용되지 않는 발행 방법입니다.");
    }
}
