package store.cookshoong.www.cookshoongbackend.menu_order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 옵션 그룹 조회 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
public class SelectOptionGroupResponseDto {
    private final Long id;
    private final Long storeId;
    private final String name;
    private final Integer minSelectCount;
    private final Integer maxSelectCount;
    private final Boolean isDeleted;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id                    the id
     * @param storeId               the storeId
     * @param name                  the name
     * @param minSelectCount        the minSelectCount
     * @param maxSelectCount        the maxSelectCount
     * @param isDeleted             the isDeleted
     */
    @QueryProjection
    public SelectOptionGroupResponseDto(Long id, Long storeId, String name, Integer minSelectCount, Integer maxSelectCount, Boolean isDeleted) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.minSelectCount = minSelectCount;
        this.maxSelectCount = maxSelectCount;
        this.isDeleted = isDeleted;
    }
}
