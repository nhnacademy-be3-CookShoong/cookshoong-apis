package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;

/**
 * 쿠폰 사용처 가맹점 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface CouponUsageMerchantRepository extends JpaRepository<CouponUsageMerchant, Long> {

}
