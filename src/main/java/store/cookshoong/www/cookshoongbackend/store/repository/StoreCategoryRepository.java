package store.cookshoong.www.cookshoongbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreCategory;
/**
 * 매장 카테고리 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, String> {
}
