package store.cookshoong.www.cookshoongbackend.store.exception.category;

/**
 * 카테고리 중복 발생 예외.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
public class DuplicatedStoreCategoryException extends RuntimeException {
    public DuplicatedStoreCategoryException(String categoryName) {
        super(categoryName + "은 이미 등록되어 있습니다.");
    }
}
