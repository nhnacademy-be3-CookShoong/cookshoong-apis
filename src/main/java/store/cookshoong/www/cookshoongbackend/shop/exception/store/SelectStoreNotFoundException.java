package store.cookshoong.www.cookshoongbackend.shop.exception.store;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 회원 권한 요청시 발생하는 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class SelectStoreNotFoundException extends NotFoundException {
    public SelectStoreNotFoundException() {
        super("매장이 존재하지 않습니다.");
    }
}
