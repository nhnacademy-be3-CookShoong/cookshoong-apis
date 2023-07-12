package store.cookshoong.www.cookshoongbackend.store.exception.category;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * 카테고리 찾을 수 없는 예외.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
public class StoreCategoryNotFoundException extends NotFoundException {
    public StoreCategoryNotFoundException() {
        super("해당 카테고리는 존재하지 않습니다.");
    }
}
