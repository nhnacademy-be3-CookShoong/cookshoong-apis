package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;

/**
 * 쿠폰 타입 할인률 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface CouponTypePercentRepository extends JpaRepository<CouponTypePercent, Integer> {
    Optional<CouponTypePercent> findByRateAndMinimumPriceAndMaximumPrice(BigDecimal rate, int minimumPrice,
                                                                         int maximumPrice);
}
