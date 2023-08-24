package store.cookshoong.www.cookshoongbackend.delivery.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.delivery.model.event.DeliveryEvent;
import store.cookshoong.www.cookshoongbackend.delivery.service.DeliveryService;

/**
 * 배송 이벤트 리스너.
 *
 * @author eora21 (김주호)
 * @since 2023.08.23
 */
@Component
@RequiredArgsConstructor
public class DeliveryEventListener implements ApplicationListener<DeliveryEvent> {
    private final DeliveryService deliveryService;

    @Async
    @Override
    public void onApplicationEvent(DeliveryEvent event) {
        deliveryService.sendDelivery(event.getOrderCode());
    }
}
