package repository;

import entity.CouponLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CouponLogsRepository extends JpaRepository<CouponLogs, Long>, JpaSpecificationExecutor<CouponLogs> {

}