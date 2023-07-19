package store.cookshoong.www.cookshoongbackend.menu_order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.file.Image;

/**
 * 메뉴 조회 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
public class SelectMenuResponseDto {
    private final Long id;
    private final String menuStatus;
    private final Long storeId;
    private final String name;
    private final Integer price;
    private final String description;
    private final Image image;
    private final Integer cookingTime;
    private final BigDecimal earningRate;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id                 the id
     * @param menuStatus         the menuStatus
     * @param storeId            the storeId
     * @param name               the name
     * @param price              the price
     * @param description        the description
     * @param image              the image
     * @param cookingTime        the cookingTime
     * @param earningRate        the earningRate
     */
    @QueryProjection

    public SelectMenuResponseDto(Long id, String menuStatus, Long storeId, String name, Integer price, String description, Image image, Integer cookingTime, BigDecimal earningRate) {
        this.id = id;
        this.menuStatus = menuStatus;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.cookingTime = cookingTime;
        this.earningRate = earningRate;
    }
}
