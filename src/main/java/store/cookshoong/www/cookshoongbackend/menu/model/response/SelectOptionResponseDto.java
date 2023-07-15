package store.cookshoong.www.cookshoongbackend.menu.model.response;

import com.querydsl.core.annotations.QueryProjection;

/**
 * 사업자입장에서 옵션 조회를 위한 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
public class SelectOptionResponseDto {
    private final String name;
    private final Integer price;
    private final Boolean isDeleted;
    private final Integer optionSequence;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param name             the name
     * @param price            the price
     * @param isDeleted        the isDeleted
     * @param optionSequence   the optionSequence
     */
    @QueryProjection
    public SelectOptionResponseDto(String name, Integer price, Boolean isDeleted, Integer optionSequence) {
        this.name = name;
        this.price = price;
        this.isDeleted = isDeleted;
        this.optionSequence = optionSequence;
    }
}
