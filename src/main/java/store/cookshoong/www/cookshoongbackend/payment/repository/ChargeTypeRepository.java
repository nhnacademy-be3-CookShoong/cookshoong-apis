package store.cookshoong.www.cookshoongbackend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;

public interface ChargeTypeRepository extends JpaRepository<ChargeType, Long> {
}
