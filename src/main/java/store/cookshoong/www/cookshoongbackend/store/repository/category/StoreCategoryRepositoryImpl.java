package store.cookshoong.www.cookshoongbackend.store.repository.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.store.entity.QStoreCategory;
import store.cookshoong.www.cookshoongbackend.store.model.response.QSelectAllCategoriesForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.QSelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllCategoriesForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllCategoriesResponseDto;

/**
 * 매장 카테고리 레포지토리 작성.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@RequiredArgsConstructor
public class StoreCategoryRepositoryImpl implements StoreCategoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectAllCategoriesResponseDto> lookupStoreCategoriesPage(Pageable pageable) {
        List<SelectAllCategoriesResponseDto> responseDtos = lookupStoreCategories(pageable);
        long total = lookupTotal();
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectAllCategoriesResponseDto> lookupStoreCategories(Pageable pageable) {
        QStoreCategory storeCategory = QStoreCategory.storeCategory;
        return jpaQueryFactory
            .select(new QSelectAllCategoriesResponseDto(storeCategory.categoryCode, storeCategory.description))
            .from(storeCategory)
            .orderBy(storeCategory.description.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private Long lookupTotal() {
        QStoreCategory storeCategory = QStoreCategory.storeCategory;
        return jpaQueryFactory
            .select(storeCategory.count())
            .from(storeCategory)
            .fetchOne();
    }

    @Override
    public List<SelectAllCategoriesForUserResponseDto> lookupStoreCategories() {
        QStoreCategory storeCategory = QStoreCategory.storeCategory;
        return jpaQueryFactory
            .select(new QSelectAllCategoriesForUserResponseDto(storeCategory.categoryCode, storeCategory.description))
            .from(storeCategory)
            .orderBy(storeCategory.description.asc())
            .fetch();
    }
}
