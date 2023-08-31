package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.Optional;
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
    /**
     * 사용자에게 발급되지 않은 쿠폰을 반환하는 메서드.
     *
     * @param couponPolicy the coupon policy
     * @return the list
     */
    Optional<IssueCoupon> findTopByCouponPolicyAndAccountIsNull(CouponPolicy couponPolicy);
}
