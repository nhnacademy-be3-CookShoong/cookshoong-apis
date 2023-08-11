package store.cookshoong.www.cookshoongbackend.point.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import store.cookshoong.www.cookshoongbackend.point.model.event.PointOrderAbortEvent;
import store.cookshoong.www.cookshoongbackend.point.service.PointEventService;

/**
 * 주문 중단 시 포인트 환불 이벤트 리스너.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@RequiredArgsConstructor
public class PointOrderAbortEventListener implements ApplicationListener<PointOrderAbortEvent> {
    private final PointEventService pointEventService;

    @Async
    @Override
    public void onApplicationEvent(PointOrderAbortEvent event) {
        pointEventService.refundOrderPoint(event.getOrderCode());
    }
}
