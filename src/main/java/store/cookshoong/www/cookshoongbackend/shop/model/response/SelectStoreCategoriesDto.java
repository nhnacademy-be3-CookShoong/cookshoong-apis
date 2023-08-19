package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 카테고리 조회를 위한 dto.
 *
 * @author seungyeon
 * @since 2023.08.09
 */
@Getter
public class SelectStoreCategoriesDto {
    private final String storeCategory;

    @QueryProjection
    public SelectStoreCategoriesDto(String storeCategory){
        this.storeCategory = storeCategory;
    }
}
