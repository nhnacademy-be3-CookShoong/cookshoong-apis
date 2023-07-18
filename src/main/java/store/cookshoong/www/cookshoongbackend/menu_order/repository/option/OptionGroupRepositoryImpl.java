package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.QOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 옵션 그룹 커스텀 레포지토리 구현.
 *
 * @author papel
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class OptionGroupRepositoryImpl implements OptionGroupRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectOptionGroupResponseDto> lookupOptionGroups(Long storeId) {
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectOptionGroupResponseDto(
                optionGroup.id, store.id, optionGroup.name,
                optionGroup.minSelectCount, optionGroup.maxSelectCount,
                optionGroup.isDeleted))
            .from(optionGroup)
            .innerJoin(optionGroup.store, store)
            .where(store.id.eq(storeId))
            .fetch();
    }
}
