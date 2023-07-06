package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;

/**
 * 쿠폰 사용처 모두 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface CouponUsageAllRepository extends JpaRepository<CouponUsageAll, Long> {
    Optional<CouponUsageAll> findTopByOrderByIdAsc();
}
