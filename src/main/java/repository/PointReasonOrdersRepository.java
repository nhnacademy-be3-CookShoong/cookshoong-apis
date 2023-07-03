package repository;

import entity.PointReasonOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointReasonOrdersRepository extends JpaRepository<PointReasonOrders, Long>, JpaSpecificationExecutor<PointReasonOrders> {

}