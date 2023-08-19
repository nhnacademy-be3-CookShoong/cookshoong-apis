package store.cookshoong.www.cookshoongbackend.menu_order.exception.menu;

/**
 * 해당 메뉴가 특정 매장 내에 존재하지 않을 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.03
 */
public class MenuNotInStoreException extends RuntimeException {
    public MenuNotInStoreException() {
        super("매장에 있는 메뉴가 아닙니다.");
    }
}
