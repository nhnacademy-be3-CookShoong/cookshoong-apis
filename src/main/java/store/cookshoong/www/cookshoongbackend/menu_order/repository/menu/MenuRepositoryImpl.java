package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 메뉴 커스텀 레포지토리 구현.
 *
 * @author papel
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SelectMenuResponseDto> lookupMenus(Long storeId) {
        QMenu menu = QMenu.menu;
        QMenuStatus menuStatus = QMenuStatus.menuStatus;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectMenuResponseDto(
                menu.id, menuStatus.menuStatusCode, store.id,
                menu.name, menu.price, menu.description,
                menu.image, menu.cookingTime, menu.earningRate))
            .from(menu)
            .innerJoin(menu.menuStatusCode, menuStatus)
            .innerJoin(menu.store, store)
            .where(store.id.eq(storeId))
            .fetch();
    }
}
