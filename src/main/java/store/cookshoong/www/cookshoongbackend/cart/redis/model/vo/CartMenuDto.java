package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴를 담는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartMenuDto {

    @NotNull
    private Long menuId;
    @NotBlank
    private String menuName;
    @NotBlank
    private String menuImage;
    @NotNull
    private Integer menuPrice;
    private String locationType;
    private String domainName;
}
