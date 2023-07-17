package store.cookshoong.www.cookshoongbackend.menu_order.repository.orderdetail;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.orderdetail.OrderDetailMenuOption;

/**
 * 옵션-주문상세 레포지토리.
 *
 * @author papel
 * @since 2023.07.11
 */
public interface OrderDetailMenuOptionRepository extends JpaRepository<OrderDetailMenuOption, OrderDetailMenuOption.Pk> {
}
