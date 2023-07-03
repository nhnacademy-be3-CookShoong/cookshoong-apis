package repository;

import entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long>, JpaSpecificationExecutor<CouponPolicy> {

}