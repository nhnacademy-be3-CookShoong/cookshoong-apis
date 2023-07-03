package repository;

import entity.PointReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointReasonRepository extends JpaRepository<PointReason, Long>, JpaSpecificationExecutor<PointReason> {

}