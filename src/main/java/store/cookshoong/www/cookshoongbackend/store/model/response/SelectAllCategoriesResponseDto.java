package store.cookshoong.www.cookshoongbackend.store.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 매장 카테고리 list 응답을 위한 dto.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Getter
public class SelectAllCategoriesResponseDto {
    private final String categoryCode;
    private final String categoryName;

    /**
     * 모든 카테고리 이름 dto.
     *
     * @param categoryName the category name
     */
    @QueryProjection
    public SelectAllCategoriesResponseDto(String categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }
}
