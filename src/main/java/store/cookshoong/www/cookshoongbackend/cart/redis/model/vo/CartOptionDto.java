package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 메뉴에 대한 옵션을 담는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartOptionDto {

    private Long optionId;
    private String optionName;
    private Integer optionPrice;
}
