package store.cookshoong.www.cookshoongbackend.menu_order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 메뉴 조회 Dto.
 *
 * @author papel (윤동현)
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
    @Setter
    private String savedName;
    private final Integer cookingTime;
    private final BigDecimal earningRate;
    @Setter
    private List<Long> menuGroups;
    @Setter
    private List<Long> optionGroups;

    /**
     * QueryDSL DTO Projection 을 위한 생성자.
     *
     * @param id          the id
     * @param menuStatus  the menuStatus
     * @param storeId     the storeId
     * @param name        the name
     * @param price       the price
     * @param description the description
     * @param savedName   the saved name
     * @param cookingTime the cookingTime
     * @param earningRate the earningRate
     */
    @QueryProjection
    public SelectMenuResponseDto(Long id, String menuStatus, Long storeId, String name,
                                 Integer price, String description, String savedName, Integer cookingTime, BigDecimal earningRate) {
        this.id = id;
        this.menuStatus = menuStatus;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.savedName = savedName;
        this.cookingTime = cookingTime;
        this.earningRate = earningRate;
        this.menuGroups = new ArrayList<>();
        this.optionGroups = new ArrayList<>();
    }
}
