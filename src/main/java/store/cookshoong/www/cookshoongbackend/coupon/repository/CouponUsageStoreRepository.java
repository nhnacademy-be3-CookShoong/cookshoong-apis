package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;

/**
 * 쿠폰 사용처 가게 repository.
 *
 * @author eora21
 * @since 2023.07.04
 */
public interface CouponUsageStoreRepository extends JpaRepository<CouponUsageStore, Long> {
    Optional<CouponUsageStore> findByStoreId(Long storeId);
}
