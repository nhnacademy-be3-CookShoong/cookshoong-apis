package store.cookshoong.www.cookshoongbackend.menu_order.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
}
