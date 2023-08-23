package store.cookshoong.www.cookshoongbackend.cart.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 장바구니에서 자주 사용되는 상수.
 *
 * @author jeongjewan
 * @since 2023.08.09
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartConstant {

    public static final String PHANTOM = ":phantom";
    public static final String NO_MENU = "NO_KEY";
    public static final String CART = "cartKey=";
    public static final String LOCK = "lockKey=";
}
