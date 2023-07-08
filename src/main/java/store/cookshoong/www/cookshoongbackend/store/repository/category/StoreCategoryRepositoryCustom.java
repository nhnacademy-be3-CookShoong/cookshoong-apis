package store.cookshoong.www.cookshoongbackend.store.repository.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllCategoriesResponseDto;

/**
 * 매장 카테고리 Repository Custom Interface.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@NoRepositoryBean
public interface StoreCategoryRepositoryCustom {
    /**
     * 카테고리 리스트 페이지로 구현.
     *
     * @param pageable 페이지 정보
     * @return 페이지 별로 정보 보여줌
     */
    Page<SelectAllCategoriesResponseDto> lookupStoreCategoriesPage(Pageable pageable);
}
