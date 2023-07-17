package store.cookshoong.www.cookshoongbackend.menu.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 옵션 조회를 위한 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
public class SelectOptionResponseDto {
    private final Long id;
    private final String name;
    private final Integer price;
    private final Boolean isDeleted;
    private final Integer optionSequence;
    private final Long optionGroupId;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id               the id
     * @param name             the name
     * @param price            the price
     * @param isDeleted        the isDeleted
     * @param optionSequence   the optionSequence
     */
    @QueryProjection
    public SelectOptionResponseDto(Long id, String name, Integer price, Boolean isDeleted, Integer optionSequence, Long optionGroupId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isDeleted = isDeleted;
        this.optionSequence = optionSequence;
        this.optionGroupId = optionGroupId;
    }
}
