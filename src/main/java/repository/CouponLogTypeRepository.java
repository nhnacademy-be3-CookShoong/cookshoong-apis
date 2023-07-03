package repository;

import entity.CouponLogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponLogTypeRepository extends JpaRepository<CouponLogType, Integer>, JpaSpecificationExecutor<CouponLogType> {

}