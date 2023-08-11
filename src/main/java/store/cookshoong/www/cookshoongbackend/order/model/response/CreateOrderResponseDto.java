package store.cookshoong.www.cookshoongbackend.order.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 주문 생성 후 반환할 값들을 담은 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.04
 */
@Getter
@AllArgsConstructor
public class CreateOrderResponseDto {
    private final int totalPrice;
}
