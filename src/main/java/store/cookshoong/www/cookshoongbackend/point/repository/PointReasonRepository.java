package store.cookshoong.www.cookshoongbackend.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReason;

/**
 * 포인트 사유 리포지토리.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
public interface PointReasonRepository extends JpaRepository<PointReason, Long> {
}
