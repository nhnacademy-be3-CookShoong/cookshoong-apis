package store.cookshoong.www.cookshoongbackend.menu_order.repository.order;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;

/**
 * 주문 레포지토리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
