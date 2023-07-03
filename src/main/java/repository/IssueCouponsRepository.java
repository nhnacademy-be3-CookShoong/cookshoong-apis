package repository;

import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IssueCouponsRepository extends JpaRepository<IssueCoupons, String>, JpaSpecificationExecutor<IssueCoupons> {

}