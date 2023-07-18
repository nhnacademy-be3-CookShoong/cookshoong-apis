package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.QMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 메뉴 그룹 커스텀 레포지토리 구현.
 *
 * @author papel
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class MenuGroupRepositoryImpl implements MenuGroupRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectMenuGroupResponseDto> lookupMenuGroups(Long storeId) {
        QMenuGroup menuGroup = QMenuGroup.menuGroup;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectMenuGroupResponseDto(
                menuGroup.id, store.id, menuGroup.name,
                menuGroup.description, menuGroup.menuGroupSequence))
            .from(menuGroup)
            .innerJoin(menuGroup.store, store)
            .where(store.id.eq(storeId))
            .fetch();
    }
}
