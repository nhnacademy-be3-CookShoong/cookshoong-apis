package repository;

import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponUsageMerchantRepository extends JpaRepository<CouponUsageMerchant, Long>, JpaSpecificationExecutor<CouponUsageMerchant> {

}