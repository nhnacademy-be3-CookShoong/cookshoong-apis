package repository;

import entity.CouponTypePercent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponTypePercentRepository extends JpaRepository<CouponTypePercent, Integer>, JpaSpecificationExecutor<CouponTypePercent> {

}