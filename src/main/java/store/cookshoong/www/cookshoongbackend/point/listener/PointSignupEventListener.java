package store.cookshoong.www.cookshoongbackend.point.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import store.cookshoong.www.cookshoongbackend.point.model.event.PointSignupEvent;
import store.cookshoong.www.cookshoongbackend.point.service.PointEventService;

/**
 * 회원 가입 포인트 제공 이벤트 리스너.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@RequiredArgsConstructor
public class PointSignupEventListener implements ApplicationListener<PointSignupEvent> {
    private final PointEventService pointEventService;

    @Async
    @Override
    public void onApplicationEvent(PointSignupEvent event) {
        pointEventService.createSignupPoint(event.getAccountId());
    }
}
