package store.cookshoong.www.cookshoongbackend.shop.exception.store;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 상태코드를 찾을 수 없는 예외.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.16
 */
public class StoreStatusNotFoundException extends NotFoundException {
    public StoreStatusNotFoundException(){
        super("해당 코드는 존재하지 않습니다.");
    }
}
