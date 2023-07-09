package store.cookshoong.www.cookshoongbackend.common.exception;


/**
 * 원하는 객체를 찾지 못했을 때의 예외.
 *
 * @author koesnam
 * @since 2023.07.06
 */
public abstract class NotFoundException extends RuntimeException {
    protected NotFoundException(String target) {
        super("존재하지 않는 " + target + "입니다");
    }
}
