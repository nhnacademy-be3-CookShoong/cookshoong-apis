package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLog;

/**
 * 쿠폰 내역 repository.
 *
 * @author eora21
 * @since 2023/07/04
 */

public interface CouponLogRepository extends JpaRepository<CouponLog, Long> {

}
