package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis 장바구니에 담기는 수에 대한 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.23
 */
@Getter
@AllArgsConstructor
public class CartMenuCountDto {

    private Long count;

}
