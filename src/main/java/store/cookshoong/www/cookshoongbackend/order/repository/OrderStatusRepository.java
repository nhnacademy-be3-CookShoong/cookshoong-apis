package store.cookshoong.www.cookshoongbackend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;

/**
 * 주문 상태 레포지토리.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {
}
