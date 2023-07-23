package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;

/**
 * 쿠폰 발행 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface IssueCouponRepository extends JpaRepository<IssueCoupon, UUID>, IssueCouponRepositoryCustom {
    List<IssueCoupon> findTop10ByCouponPolicyAndAccountIsNull(CouponPolicy couponPolicy);
}
