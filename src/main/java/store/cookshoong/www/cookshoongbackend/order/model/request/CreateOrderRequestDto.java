package store.cookshoong.www.cookshoongbackend.order.model.request;

import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문을 생성할 때 사용하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.02
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOrderRequestDto {
    @NotNull
    private UUID orderCode;
    @NotNull
    private Long accountId;
    @NotNull
    private Long storeId;
    @NotNull
    private String deliveryAddress;
    private String memo;
    private UUID issueCouponCode;
    @Setter
    private Integer pointAmount;
    @Setter
    private int deliveryCost;

    /**
     * pointAmount null이거나 음수라면 0으로 반환.
     *
     * @return the point amount
     */
    public int getPointAmount() {
        if (Objects.isNull(pointAmount) || pointAmount < 0) {
            return 0;
        }
        return pointAmount;
    }
}
