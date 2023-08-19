package store.cookshoong.www.cookshoongbackend.cart.db.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * DB 에 들어있는 Options Dto.
 *
 * @author jeongjewan
 * @since 2023.07.28
 */
@Getter
public class CartOptionResponseDto {
    private final Long optionId;
    private final String name;
    private final Integer price;

    /**
     * 메뉴에 대한 옵션 정보들에 대한 생성자.
     *
     * @param optionsId     option Id
     * @param name          option Name
     * @param price         option Price
     */
    @QueryProjection
    public CartOptionResponseDto(Long optionsId, String name, Integer price) {
        this.optionId = optionsId;
        this.name = name;
        this.price = price;
    }
}
