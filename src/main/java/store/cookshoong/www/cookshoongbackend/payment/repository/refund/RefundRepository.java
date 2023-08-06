package store.cookshoong.www.cookshoongbackend.payment.repository.refund;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.Refund;

/**
 * 환불에 대한 Repository.
 *
 * @author jeongjewan
 * @since 2023.08.04
 */
public interface RefundRepository extends JpaRepository<Refund, UUID>, RefundRepositoryCustom {
}
