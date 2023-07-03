package repository;

import entity.CouponTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponTypesRepository extends JpaRepository<CouponTypes, Integer>, JpaSpecificationExecutor<CouponTypes> {

}