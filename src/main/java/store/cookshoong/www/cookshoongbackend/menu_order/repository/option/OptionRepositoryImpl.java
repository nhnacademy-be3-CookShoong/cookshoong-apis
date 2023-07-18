package store.cookshoong.www.cookshoongbackend.menu_order.repository.option;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.QOption;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.QOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 옵션 커스텀 레포지토리 구현.
 *
 * @author papel
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectOptionResponseDto> lookupOptions(Long storeId) {
        QOption option = QOption.option;
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectOptionResponseDto(
                option.id, optionGroup.id, option.name,
                option.price, option.isDeleted,
                option.optionSequence))
            .from(option)
            .innerJoin(option.optionGroup, optionGroup)
            .innerJoin(optionGroup.store, store)
            .where(store.id.eq(storeId))
            .fetch();
    }
}
