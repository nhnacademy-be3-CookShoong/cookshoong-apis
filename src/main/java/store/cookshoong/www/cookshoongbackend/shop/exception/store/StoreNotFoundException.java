package store.cookshoong.www.cookshoongbackend.shop.exception.store;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 매장 요청시 발생하는 예외.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.05
 */
public class StoreNotFoundException extends NotFoundException {
    public StoreNotFoundException() {
        super("매장");
    }
}
