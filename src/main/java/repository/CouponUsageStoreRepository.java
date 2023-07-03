package repository;

import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponUsageStoreRepository extends JpaRepository<CouponUsageStore, Long>, JpaSpecificationExecutor<CouponUsageStore> {

}