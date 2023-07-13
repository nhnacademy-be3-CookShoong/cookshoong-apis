package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 사업자 : 카테고리 등록시 사용되는 dto.
 *
 * @author seungyeon
 * @since 2023.07.12
 */
@Getter
public class SelectAllCategoriesForUserResponseDto {
    private final String categoryCode;
    private final String categoryName;

    /**
     * 모든 카테고리 이름 dto.
     *
     * @param categoryName the category name
     */
    @QueryProjection
    public SelectAllCategoriesForUserResponseDto(String categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }
}
