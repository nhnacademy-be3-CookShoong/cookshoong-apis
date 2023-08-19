package store.cookshoong.www.cookshoongbackend.cart.db.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * DB 에 들어있는 Menu Dto.
 *
 * @author jeongjewan
 * @since 2023.07.28
 */
@Getter
public class CartMenuResponseDto {
    private final Long menuId;
    private final String name;
    private final String savedName;
    private final String locationType;
    private final String domainName;
    private final Integer price;
    private final Long createTimeMillis;
    private final Integer count;

    /**
     * 장바구니에 담길 메뉴에 대한 생성자.
     *
     * @param menuId            menu Id
     * @param name              menu Name
     * @param savedName         menu Image
     * @param price             menu Price
     * @param createTimeMillis  menu createTimeMillis
     * @param count             menu count
     */
    @QueryProjection
    public CartMenuResponseDto(Long menuId, String name, String savedName,
                               Integer price, Long createTimeMillis, Integer count,
                               String locationType, String domainName) {

        this.menuId = menuId;
        this.name = name;
        this.savedName = savedName;
        this.price = price;
        this.createTimeMillis = createTimeMillis;
        this.count = count;
        this.locationType = locationType;
        this.domainName = domainName;
    }
}
