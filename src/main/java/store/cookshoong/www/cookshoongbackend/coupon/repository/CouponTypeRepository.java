package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;

/**
 * 쿠폰 타입 repository.
 *
 * @author eora21
 * @since 2023/07/04
 */

public interface CouponTypeRepository extends JpaRepository<CouponType, Integer> {

}
