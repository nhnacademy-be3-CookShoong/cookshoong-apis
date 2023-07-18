package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;

/**
 * 쿠폰 타입 금액 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface CouponTypeCashRepository extends JpaRepository<CouponTypeCash, Integer> {
    Optional<CouponTypeCash> findByDiscountAmountAndMinimumOrderPrice(int discountAmount, int minimumOrderPrice);
}
