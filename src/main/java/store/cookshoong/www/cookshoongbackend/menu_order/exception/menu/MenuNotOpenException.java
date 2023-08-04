package store.cookshoong.www.cookshoongbackend.menu_order.exception.menu;

/**
 * 메뉴가 판매중이 아닐 때 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.04
 */
public class MenuNotOpenException extends RuntimeException {
    public MenuNotOpenException() {
        super("판매중인 메뉴가 아닙니다.");
    }
}
