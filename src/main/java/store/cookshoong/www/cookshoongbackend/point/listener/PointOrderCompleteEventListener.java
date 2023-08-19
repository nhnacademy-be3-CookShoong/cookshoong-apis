package store.cookshoong.www.cookshoongbackend.point.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.point.model.event.PointOrderCompleteEvent;
import store.cookshoong.www.cookshoongbackend.point.service.PointEventService;

/**
 * 주문 완료 시 포인트 제공 이벤트 리스너.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Component
@RequiredArgsConstructor
public class PointOrderCompleteEventListener implements ApplicationListener<PointOrderCompleteEvent> {
    private final PointEventService pointEventService;

    @Async
    @Override
    public void onApplicationEvent(PointOrderCompleteEvent event) {
        pointEventService.createPaymentPoint(event.getOrderCode());
    }
}
