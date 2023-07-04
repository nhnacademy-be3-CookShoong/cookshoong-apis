package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;
/**
 * 매장 상태 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */

public interface StoreStatusRepository extends JpaRepository<StoreStatus, String> {
}
