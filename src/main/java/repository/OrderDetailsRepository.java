package repository;

import entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long>, JpaSpecificationExecutor<OrderDetails> {

}