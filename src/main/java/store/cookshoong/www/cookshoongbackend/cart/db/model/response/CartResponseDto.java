package store.cookshoong.www.cookshoongbackend.cart.db.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장바구니 정보가져오는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.28
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long accountId;
    private Long storeId;
    private String name;
    private Integer deliveryCost;
    private Integer minimumOrderPrice;
    private CartMenuResponseDto cartMenuResponseDto;
    private List<CartOptionResponseDto> cartOptionResponseDto;
}
