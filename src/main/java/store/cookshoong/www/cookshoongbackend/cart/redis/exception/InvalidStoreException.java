package store.cookshoong.www.cookshoongbackend.cart.redis.exception;

/**
 * Redis 장바구니에 저장된 매장이 같지 않으면 생기는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.21
 */
public class InvalidStoreException extends RuntimeException {

    private static final String MESSAGE = "장바구니에는 일치한 매장의 메뉴만 담을 수 있습니다.";

    public InvalidStoreException() {
        super(MESSAGE);
    }
}
