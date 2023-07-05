package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;

/**
 * 매장 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoreRepository extends JpaRepository<Store, Long> {
}
