package store.cookshoong.www.cookshoongbackend.menu_order.model.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 메뉴 그룹 조회 Dto.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Getter
public class SelectMenuGroupResponseDto {
    private final Long id;
    private final Long storeId;
    private final String name;
    private final String description;
    private final Integer menuGroupSequence;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id                  the id
     * @param storeId             the storeId
     * @param name                the name
     * @param description         the description
     * @param menuGroupSequence   the menuGroupSequence
     */
    @QueryProjection
    public SelectMenuGroupResponseDto(Long id, Long storeId, String name, String description, Integer menuGroupSequence) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.menuGroupSequence = menuGroupSequence;
    }
}
