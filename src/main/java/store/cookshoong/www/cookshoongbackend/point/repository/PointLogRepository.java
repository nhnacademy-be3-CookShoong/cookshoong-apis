package store.cookshoong.www.cookshoongbackend.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.point.entity.PointLog;

/**
 * 포인트 로그 리포지토리.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
public interface PointLogRepository extends JpaRepository<PointLog, Long>, PointLogRepositoryCustom {
}
