package store.cookshoong.www.cookshoongbackend.review.model;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;

/**
 * 리뷰 작성가능한 상태확인 매니저.
 *
 * @author seungyeon
 * @since 2023.08.14
 */
@Component
public class ReviewStatusManager {
    private final Set<String> reviewWritableStatus = new HashSet<>();

    /**
     * 리뷰 작성가능한 상태 : 배달완료, 환불처리 등.
     */
    public ReviewStatusManager() {
        reviewWritableStatus.add(OrderStatus.StatusCode.COMPLETE.name());
        reviewWritableStatus.add(OrderStatus.StatusCode.PARTIAL.name());
    }

    /**
     * 리뷰 작성가능한 상태에 포함되는지 확인.
     *
     * @param status the status
     * @return the boolean
     */
    public boolean isReviewWritableStatus(String status) {
        return reviewWritableStatus.contains(status);
    }
}
