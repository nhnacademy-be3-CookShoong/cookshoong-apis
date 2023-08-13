package store.cookshoong.www.cookshoongbackend.order.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 주문 가능 여부 반환 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.12
 */
@Getter
@AllArgsConstructor
public class SelectOrderPossibleResponseDto {
    boolean orderPossible;
    String explain;
}
