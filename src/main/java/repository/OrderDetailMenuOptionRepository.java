package repository;

import store.cookshoong.www.cookshoongbackend.order.OrderDetailMenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDetailMenuOptionRepository extends JpaRepository<OrderDetailMenuOption, Long>, JpaSpecificationExecutor<OrderDetailMenuOption> {

}