package store.cookshoong.www.cookshoongbackend.coupon.model.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 중지에 의한 쿠폰 사용 취소 이벤트.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Getter
public class CouponOrderAbortEvent extends ApplicationEvent {
    private final UUID orderCode;

    /**
     * Instantiates a new Point order event.
     *
     * @param source    the source
     * @param orderCode the order code
     */
    public CouponOrderAbortEvent(Object source, UUID orderCode) {
        super(source);
        this.orderCode = orderCode;
    }
}
