package store.cookshoong.www.cookshoongbackend.menu.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * 메뉴 조회를 위한 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
public class SelectMenuResponseDto {
    private final Long id;
    private final String name;
    private final Integer price;
    private final String description;
    private final String image;
    private final Integer cookingTime;
    private final BigDecimal earningRate;
    private final String menuStatus;
    private final Integer menuSequence;
    private final Long menuGroupId;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id                 the id
     * @param name               the name
     * @param price              the price
     * @param description        the description
     * @param image              the image
     * @param cookingTime        the cookingTime
     * @param earningRate        the earningRate
     * @param menuStatus         the menuStatus
     * @param menuSequence       the menuSequence
     * @param menuGroupId        the menuGroup
     */
    @QueryProjection
    public SelectMenuResponseDto(Long id, String name, Integer price, String description, String image, Integer cookingTime, BigDecimal earningRate, String menuStatus, Integer menuSequence, Long menuGroupId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.cookingTime = cookingTime;
        this.earningRate = earningRate;
        this.menuStatus = menuStatus;
        this.menuSequence = menuSequence;
        this.menuGroupId = menuGroupId;
    }
}
