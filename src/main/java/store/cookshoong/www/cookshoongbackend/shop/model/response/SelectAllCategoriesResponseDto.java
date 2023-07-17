package store.cookshoong.www.cookshoongbackend.shop.model.response;

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
    private final String description;

    /**
     * 모든 카테고리 이름 dto.
     *
     * @param categoryCode the category code
     * @param description  the description
     */
    @QueryProjection
    public SelectAllCategoriesResponseDto(String categoryCode, String description) {
        this.categoryCode = categoryCode;
        this.description = description;
    }
}
