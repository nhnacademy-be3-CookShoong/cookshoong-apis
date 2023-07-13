package store.cookshoong.www.cookshoongbackend.shop.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;

/**
 * 매장 상태 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoreStatusRepository extends JpaRepository<StoreStatus, String> {
}
