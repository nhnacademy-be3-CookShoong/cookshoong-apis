package store.cookshoong.www.cookshoongbackend.shop.repository.category;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoresHasCategory;

/**
 * 매장-카테고리 레포지토리.
 *
 * @author seungyeon
 * @since 2023.07.04
 */
public interface StoresHasCategoryRepository extends JpaRepository<StoresHasCategory, StoresHasCategory.Pk> {
    void deleteAllByPk_StoreId(Long storeId);
    List<StoresHasCategory> findAllByPk_StoreId(Long storeId);
}
