package store.cookshoong.www.cookshoongbackend.store.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 회원 권한 요청시 발생하는 예외.
 *
 * @author koesnam
 * @since 2023.07.05
 */
public class SelectStoreNotFoundException extends NotFoundException {
    public SelectStoreNotFoundException(Long storeId) {
        super(storeId + "와 일치하는 매장을 찾을 수 없습니다.");
    }
}
