package repository;

import store.cookshoong.www.cookshoongbackend.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, String>, JpaSpecificationExecutor<OrderStatus> {

}