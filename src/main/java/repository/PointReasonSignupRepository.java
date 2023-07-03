package repository;

import entity.PointReasonSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointReasonSignupRepository extends JpaRepository<PointReasonSignup, Long>, JpaSpecificationExecutor<PointReasonSignup> {

}