package store.cookshoong.www.cookshoongbackend.point.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReasonOrder;

/**
 * 포인트 주문 리포지토리.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
public interface PointReasonOrderRepository extends JpaRepository<PointReasonOrder, Long> {
    List<PointReasonOrder> findAllByOrder(Order order);

    boolean existsByOrderCode(UUID orderCode);
}
