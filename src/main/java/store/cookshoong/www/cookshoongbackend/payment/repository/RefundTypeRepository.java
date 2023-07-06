package store.cookshoong.www.cookshoongbackend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.RefundType;

public interface RefundTypeRepository extends JpaRepository<RefundType, Long> {
}
