package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 먹은 메뉴에 대한 이름을 확인하기 위한 dto.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
public class SelectReviewOrderMenuResponseDto {
    private final Long menuId;
    private final String menuName;
    @QueryProjection
    public SelectReviewOrderMenuResponseDto(Long menuId, String menuName){
        this.menuId = menuId;
        this.menuName = menuName;
    }
}
