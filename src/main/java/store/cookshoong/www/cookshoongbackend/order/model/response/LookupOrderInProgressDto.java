package store.cookshoong.www.cookshoongbackend.order.model.response;

import com.querydsl.core.annotations.QueryProjection;
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
public class LookupOrderInProgressDto {
    private final UUID orderCode;
    private final String orderStatusCode;
    private final List<LookupOrderDetailDto> selectOrderDetails;
    private final String memo;
    private final UUID chargeCode;

    /**
     * Instantiates a new Select order in progress dto.
     *
     * @param orderCode          the order code
     * @param orderStatusCode    the order status code
     * @param selectOrderDetails the select order details
     * @param memo               the memo
     * @param chargeCode         the charge id
     */
    @QueryProjection
    public LookupOrderInProgressDto(UUID orderCode, String orderStatusCode,
                                    List<LookupOrderDetailDto> selectOrderDetails, String memo, UUID chargeCode) {
        this.orderCode = orderCode;
        this.orderStatusCode = orderStatusCode;
        this.selectOrderDetails = selectOrderDetails;
        this.memo = memo;
        this.chargeCode = chargeCode;
    }
}
