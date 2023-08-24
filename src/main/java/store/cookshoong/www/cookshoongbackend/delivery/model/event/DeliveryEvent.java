package store.cookshoong.www.cookshoongbackend.delivery.model.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 배송 이벤트.
 *
 * @author eora21 (김주호)
 * @since 2023.08.23
 */
@Getter
public class DeliveryEvent extends ApplicationEvent {
    private final UUID orderCode;

    /**
     * Instantiates a new Point order event.
     *
     * @param source    the source
     * @param orderCode the order code
     */
    public DeliveryEvent(Object source, UUID orderCode) {
        super(source);
        this.orderCode = orderCode;
    }
}
