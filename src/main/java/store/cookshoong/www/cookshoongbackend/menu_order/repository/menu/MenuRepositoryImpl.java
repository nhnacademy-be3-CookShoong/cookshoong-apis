package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.QMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.QMenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;

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
        QMenuHasMenuGroup menuHasMenuGroup = QMenuHasMenuGroup.menuHasMenuGroup;
        QMenuGroup menuGroup = QMenuGroup.menuGroup;
        QMenuStatus status = QMenuStatus.menuStatus;

        return jpaQueryFactory
            .select(new QSelectMenuResponseDto(
                menu.id, menu.name, menu.price, menu.description,
                menu.image, menu.cookingTime, menu.earningRate,
                status.menuStatusCode, menuHasMenuGroup.menuSequence,
                menuGroup.id))
            .from(menu)
            .innerJoin(menu.menuStatusCode, status)
            .innerJoin(menu.menuHasMenuGroups, menuHasMenuGroup)
            .innerJoin(menuHasMenuGroup.menuGroup, menuGroup)
            .where(menu.store.id.eq(storeId))
            .fetch();
    }
}
