package store.cookshoong.www.cookshoongbackend.review.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 리뷰 작성에 대한 이벤트.
 *
 * @author eora21 (김주호)
 * @since 2023.08.16
 */
@Getter
public class ReviewEvent extends ApplicationEvent {
    private final Long reviewId;

    /**
     * Instantiates a new Point order event.
     *
     * @param source   the source
     * @param reviewId the review id
     */
    public ReviewEvent(Object source, Long reviewId) {
        super(source);
        this.reviewId = reviewId;
    }
}
