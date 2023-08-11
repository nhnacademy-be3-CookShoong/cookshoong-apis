package store.cookshoong.www.cookshoongbackend.order.model.request;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;

/**
 * 주문 상태를 변경할 때 사용하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.02
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchOrderRequestDto {
    @NotNull
    private UUID orderCode;
    @NotNull
    private OrderStatus.StatusCode statusCode;
}
