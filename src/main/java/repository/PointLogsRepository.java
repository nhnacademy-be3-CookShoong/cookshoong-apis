package repository;

import entity.PointLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointLogsRepository extends JpaRepository<PointLogs, Long>, JpaSpecificationExecutor<PointLogs> {

}