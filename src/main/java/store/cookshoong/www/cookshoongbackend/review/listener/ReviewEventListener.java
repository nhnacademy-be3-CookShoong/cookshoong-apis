package store.cookshoong.www.cookshoongbackend.review.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.point.service.PointEventService;
import store.cookshoong.www.cookshoongbackend.review.model.event.ReviewEvent;

/**
 * 리뷰 작성 이벤트 리스너.
 *
 * @author eora21 (김주호)
 * @since 2023.08.16
 */
@Component
@RequiredArgsConstructor
public class ReviewEventListener implements ApplicationListener<ReviewEvent> {
    private final PointEventService pointEventService;

    @Async
    @Override
    public void onApplicationEvent(ReviewEvent event) {
        pointEventService.createReviewPoint(event.getReviewId());
    }
}
