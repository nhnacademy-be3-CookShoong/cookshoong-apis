package store.cookshoong.www.cookshoongbackend.store.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.store.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.category.StoreCategoryRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * 매장 카테고리 레포지토리 테스트.
 *
 * @author seungyeon
 * @since 2023.07.09
 */
@DataJpaTest
@Import({QueryDslConfig.class, TestEntity.class})
class CategoryRepositoryTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestEntity testEntity;

    @Autowired
    TestEntityManager em;
    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Test
    @DisplayName("매장 카테고리 조회 - 성공")
    void select_category() {
        StoreCategory actual = new StoreCategory("CHK", "치킨");
        String actualName = storeCategoryRepository.save(actual).getDescription();

        StoreCategory expect = storeCategoryRepository.findByDescription(actualName).orElseThrow();

        assertThat(expect.getCategoryCode()).isEqualTo(actual.getCategoryCode());
        assertThat(expect.getDescription()).isEqualTo(actual.getDescription());
    }

    @Test
    @DisplayName("매장 카테고리 저장 - 성공")
    void create_category() {
        StoreCategory actual = testEntity.getStoreCategory();
        storeCategoryRepository.save(actual);

        StoreCategory expect = em.find(StoreCategory.class, actual.getCategoryCode());

        assertThat(expect.getCategoryCode()).isEqualTo(actual.getCategoryCode());
        assertThat(expect.getDescription()).isEqualTo(actual.getDescription());
    }

    @Test
    @DisplayName("매장 카테고리 존재여부 - 성공")
    void exist_by_description() {
        StoreCategory storeCategory = testEntity.getStoreCategory();
        storeCategoryRepository.save(storeCategory);

        boolean expect = storeCategoryRepository.existsByDescription(storeCategory.getDescription());

        assertThat(expect).isTrue();
    }

    @Test
    @DisplayName("매장 카테고리 리스트 조회(페이지) - 성공")
    void select_categories_page() {
        for (int i = 1; i < 20; i++) {
            StoreCategory storeCategory = new StoreCategory("Code" + i, "카테고리" + i);
            storeCategoryRepository.save(storeCategory);
        }
        storeCategoryRepository.flush();

        Pageable pageable = PageRequest.of(2, 3);
        Page<SelectAllCategoriesResponseDto> expects = storeCategoryRepository.lookupStoreCategoriesPage(pageable);

        assertThat(expects.get().count()).isEqualTo(3);
        assertThat(expects.get().findFirst().get().getCategoryName()).isEqualTo("카테고리7");
    }

    @Test
    @DisplayName("카테고리 조회 - 실패")
    void select_bank_fail() {
        assertThatThrownBy(() -> storeCategoryRepository.findByDescription("noCategory")
            .orElseThrow(StoreCategoryNotFoundException::new))
            .isInstanceOf(StoreCategoryNotFoundException.class)
            .hasMessageContaining("해당 카테고리는 존재하지 않습니다.");
    }
}
