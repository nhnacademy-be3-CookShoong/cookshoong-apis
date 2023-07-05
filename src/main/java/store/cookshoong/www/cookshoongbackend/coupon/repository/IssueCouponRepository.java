package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;

/**
 * 쿠폰 발행 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface IssueCouponRepository extends JpaRepository<IssueCoupon, String> {

}
