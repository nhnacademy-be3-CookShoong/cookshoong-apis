package repository;

import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long>, JpaSpecificationExecutor<CouponUsage> {

}