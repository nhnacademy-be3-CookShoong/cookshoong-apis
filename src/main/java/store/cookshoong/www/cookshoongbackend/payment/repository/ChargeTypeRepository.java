package store.cookshoong.www.cookshoongbackend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;

/**
 * 결제 타입에 해당되는 JpaRepository.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
public interface ChargeTypeRepository extends JpaRepository<ChargeType, Long> {
}
