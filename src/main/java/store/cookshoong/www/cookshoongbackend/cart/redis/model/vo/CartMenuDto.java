package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 메뉴를 담는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartMenuDto {

    @NotNull
    private Long menuId;
    private String menuName;
    private String menuImage;
    private int menuPrice;
}
