package store.cookshoong.www.cookshoongbackend.point.model.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 주문 완료 시 포인트 제공 이벤트.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
public class PointOrderCompleteEvent extends ApplicationEvent {
    private final UUID orderCode;

    /**
     * Instantiates a new Point order event.
     *
     * @param source    the source
     * @param orderCode the order code
     */
    public PointOrderCompleteEvent(Object source, UUID orderCode) {
        super(source);
        this.orderCode = orderCode;
    }
}
