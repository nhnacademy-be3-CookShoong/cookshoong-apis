package store.cookshoong.www.cookshoongbackend.menu_order.model.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 사업자입장에서 메뉴 그룹 조회를 위한 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
public class SelectMenuGroupResponseDto {
    private final String name;
    private final String description;
    private final Integer menuGroupSequence;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param name                the name
     * @param description         the description
     * @param menuGroupSequence   the menuGroupSequence
     */
    @QueryProjection
    public SelectMenuGroupResponseDto(String name, String description, Integer menuGroupSequence) {
        this.name = name;
        this.description = description;
        this.menuGroupSequence = menuGroupSequence;
    }
}
