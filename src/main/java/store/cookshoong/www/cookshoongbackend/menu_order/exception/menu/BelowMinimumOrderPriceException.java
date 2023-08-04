package store.cookshoong.www.cookshoongbackend.menu_order.exception.menu;

/**
 * 주문에 적용한 쿠폰의 최소 주문 금액을 충족하지 못 한 경우 발생하는 예외.
 *
 * @author eora21 (김주호)
 * @since 2023.08.03
 */
public class BelowMinimumOrderPriceException extends RuntimeException {
    public BelowMinimumOrderPriceException() {
        super("적용한 쿠폰의 최소 주문 금액을 충족하지 못 했습니다.");
    }
}
