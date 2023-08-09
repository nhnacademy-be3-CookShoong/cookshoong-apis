package store.cookshoong.www.cookshoongbackend.payment.repository.charge;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;

/**
 * 결제에 관한 Repository.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
public interface ChargeRepository extends JpaRepository<Charge, UUID>, ChargeRepositoryCustom {
    Optional<Charge> findByOrder(Order order);
}
