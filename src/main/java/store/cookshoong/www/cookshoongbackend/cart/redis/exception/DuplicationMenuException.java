package store.cookshoong.www.cookshoongbackend.cart.redis.exception;

/**
 * Redis 장바구니 안에 추가 또는 수정할 때 일치한 메뉴가 존재할 경우에 관한 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.21
 */
public class DuplicationMenuException extends RuntimeException {

    private static final String MESSAGE = "장바구니에 일치한 메뉴가 있습니다. 확인해주세요.";

    public DuplicationMenuException() {
        super(MESSAGE);
    }
}
