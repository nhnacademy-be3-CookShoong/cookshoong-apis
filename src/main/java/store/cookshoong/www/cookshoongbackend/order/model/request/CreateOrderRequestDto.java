package store.cookshoong.www.cookshoongbackend.order.model.request;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문을 생성할 때 사용하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.02
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOrderRequestDto {
    private UUID orderCode;
    private Long accountId;
    private Long storeId;
    private String memo;
    private UUID issueCouponCode;
    private Integer pointAmount;
}
