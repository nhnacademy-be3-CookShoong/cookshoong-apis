package store.cookshoong.www.cookshoongbackend.point.model.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 중단 시 포인트 환급 이벤트.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Getter
public class PointOrderAbortEvent extends ApplicationEvent {
    private final UUID orderCode;

    /**
     * Instantiates a new Point order event.
     *
     * @param source    the source
     * @param orderCode the order code
     */
    public PointOrderAbortEvent(Object source, UUID orderCode) {
        super(source);
        this.orderCode = orderCode;
    }
}
