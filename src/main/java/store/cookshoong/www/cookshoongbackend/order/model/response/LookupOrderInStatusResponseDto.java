package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

/**
 * 주문 정보를 확인하는 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Getter
public class LookupOrderInStatusResponseDto {
    private final UUID orderCode;
    private final String orderStatusDescription;
    private final List<LookupOrderDetailMenuResponseDto> selectOrderDetails;
    private final String memo;
    private final UUID chargeCode;
    private final int chargedAmount;
    private final String paymentKey;
    private final LocalDateTime orderedAt;
    private final String deliveryAddress;

    /**
     * Instantiates a new Select order in progress dto.
     *
     * @param orderCode          the order code
     * @param orderStatusDescription the order status explain
     * @param selectOrderDetails the select order details
     * @param memo               the memo
     * @param chargeCode         the charge id
     * @param chargedAmount      the charged amount
     * @param paymentKey         the payment key
     * @param orderedAt          the ordered at
     * @param deliveryAddress    the delivery address
     */
    @QueryProjection
    public LookupOrderInStatusResponseDto(UUID orderCode, String orderStatusDescription,
                                          List<LookupOrderDetailMenuResponseDto> selectOrderDetails, String memo,
                                          UUID chargeCode, int chargedAmount, String paymentKey,
                                          LocalDateTime orderedAt, String deliveryAddress) {
        this.orderCode = orderCode;
        this.orderStatusDescription = orderStatusDescription;
        this.selectOrderDetails = selectOrderDetails;
        this.memo = memo;
        this.chargeCode = chargeCode;
        this.chargedAmount = chargedAmount;
        this.paymentKey = paymentKey;
        this.orderedAt = orderedAt;
        this.deliveryAddress = deliveryAddress;
    }
}
