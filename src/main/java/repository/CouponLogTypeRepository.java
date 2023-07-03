package repository;

import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponLogTypeRepository extends JpaRepository<CouponLogType, Integer>, JpaSpecificationExecutor<CouponLogType> {

}