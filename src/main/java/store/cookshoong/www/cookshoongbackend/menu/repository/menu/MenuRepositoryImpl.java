package store.cookshoong.www.cookshoongbackend.menu.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu.entity.menu.QMenu;
import store.cookshoong.www.cookshoongbackend.menu.entity.menu.QMenuStatus;
import store.cookshoong.www.cookshoongbackend.menu.entity.menugroup.QMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu.entity.menugroup.QMenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu.model.response.QSelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu.model.response.SelectMenuResponseDto;

/**
 * 메뉴 Custom 레포지토리 구현.
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
