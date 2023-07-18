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
    private final String name;
    private final Integer minSelectCount;
    private final Integer maxSelectCount;
    private final Boolean isDeleted;
    private final Integer optionGroupSequence;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param name                  the name
     * @param minSelectCount        the minSelectCount
     * @param maxSelectCount        the maxSelectCount
     * @param isDeleted             the isDeleted
     * @param optionGroupSequence   the optionGroupSequence
     */
    @QueryProjection
    public SelectOptionGroupResponseDto(String name, Integer minSelectCount, Integer maxSelectCount, Boolean isDeleted, Integer optionGroupSequence) {
        this.name = name;
        this.minSelectCount = minSelectCount;
        this.maxSelectCount = maxSelectCount;
        this.isDeleted = isDeleted;
        this.optionGroupSequence = optionGroupSequence;
    }
}
