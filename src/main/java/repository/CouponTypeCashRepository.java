package repository;

import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponTypeCashRepository extends JpaRepository<CouponTypeCash, Integer>, JpaSpecificationExecutor<CouponTypeCash> {

}