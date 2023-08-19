package store.cookshoong.www.cookshoongbackend.point.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;

/**
 * 포인트 사유 리뷰 entity.
 *
 * @author eora21 (김주호)
 * @since 2023.08.16
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("REVIEW")
@Table(name = "point_reason_review")
public class PointReasonReview extends PointReason {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    Review review;

    public PointReasonReview(Review review, String explain) {
        super(explain);
        this.review = review;
    }
}
