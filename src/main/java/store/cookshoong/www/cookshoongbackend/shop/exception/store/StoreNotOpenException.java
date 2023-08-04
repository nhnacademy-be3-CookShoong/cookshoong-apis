package store.cookshoong.www.cookshoongbackend.shop.exception.store;

/**
 * 영업중인 매장이 아닐 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.04
 */
public class StoreNotOpenException extends RuntimeException {
    public StoreNotOpenException() {
        super("영업중인 매장이 아닙니다.");
    }
}
