package store.cookshoong.www.cookshoongbackend.store.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 없는 매장 탐색 요청시 발생하는 예외
 *
 * @author papel
 * @since 2023.07.07
 */
public class StoreNotFoundException extends NotFoundException {
    public StoreNotFoundException(Long storeId) {
        super(storeId + "와 일치하는 매장을 찾을 수 없습니다.");
    }
}
