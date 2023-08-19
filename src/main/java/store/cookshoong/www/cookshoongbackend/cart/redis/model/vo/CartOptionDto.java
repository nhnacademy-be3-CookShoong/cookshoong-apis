package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetailMenuOption;

/**
 * 메뉴에 대한 옵션을 담는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartOptionDto {

    private Long optionId;
    private String optionName;
    private Integer optionPrice;

    public OrderDetailMenuOption toEntity(Option option, OrderDetail orderDetail) {
        return new OrderDetailMenuOption(option, orderDetail, option.getName(), option.getPrice());
    }
}
