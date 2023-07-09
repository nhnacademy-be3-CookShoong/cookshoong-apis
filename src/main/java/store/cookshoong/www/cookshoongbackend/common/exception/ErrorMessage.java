package store.cookshoong.www.cookshoongbackend.common.exception;

/**
 * Response 에서 에러메세지를 반환해주기위해 만들어 준 클래스.
 *
 * @author koesnam
 * @since 2023.07.08
 */
public class ErrorMessage {
    private final String message;

    public ErrorMessage(Throwable throwable) {
        this.message = throwable.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
