package store.cookshoong.www.cookshoongbackend.shop.repository.category;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;

/**
 * 매장 카테고리 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, String>, StoreCategoryRepositoryCustom {
    /**
     * 매장 카테고리 존재 여부.
     *
     * @param description 카테고리 이름
     * @return true : 존재함. false : 아직 등록되지 않음
     */
    boolean existsByDescription(String description);

    /**
     * 카테고리 이름으로 매장 카테고리 객체 반환.
     *
     * @param description 카테고리 이름
     * @return 매장 카테고리 객체 or null
     */
    Optional<StoreCategory> findByDescription(String description);
}
