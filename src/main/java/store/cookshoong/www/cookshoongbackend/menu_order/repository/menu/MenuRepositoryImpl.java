package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.file.entity.QImage;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.QMenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.QMenuHasOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.QSelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;

/**
 * 메뉴 커스텀 레포지토리 구현.
 *
 * @author papel (윤동현)
 * @since 2023.07.17
 */
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 매장 메뉴 조회.
     *
     * @param menuId 메뉴 아이디
     * @return 매장의 메뉴
     */
    @Override
    public Optional<SelectMenuResponseDto> lookupMenu(Long menuId) {
        QMenu menu = QMenu.menu;
        QMenuStatus menuStatus = QMenuStatus.menuStatus;
        QStore store = QStore.store;
        QImage image = QImage.image;
        QMenuHasMenuGroup menuHasMenuGroup = QMenuHasMenuGroup.menuHasMenuGroup;
        QMenuHasOptionGroup menuHasOptionGroup = QMenuHasOptionGroup.menuHasOptionGroup;

        SelectMenuResponseDto selectMenuResponseDto = jpaQueryFactory
            .select(new QSelectMenuResponseDto(
                menu.id, menuStatus.code, store.id,
                menu.name, menu.price, menu.description,
                menu.image.savedName, menu.cookingTime, menu.earningRate, menu.image.locationType, menu.image.domainName))
            .from(menu)
            .innerJoin(menu.menuStatus, menuStatus)
            .innerJoin(menu.store, store)
            .innerJoin(menu.image, image)
            .where(menu.id.eq(menuId))
            .fetchOne();

        List<Long> menuGroupIds = jpaQueryFactory
            .select(menuHasMenuGroup.menuGroup.id)
            .from(menuHasMenuGroup)
            .where(menuHasMenuGroup.menu.id.eq(menuId))
            .fetch();

        List<Long> optionGroupIds = jpaQueryFactory
            .select(menuHasOptionGroup.optionGroup.id)
            .from(menuHasOptionGroup)
            .where(menuHasOptionGroup.menu.id.eq(menuId))
            .fetch();

        Objects.requireNonNull(selectMenuResponseDto).setMenuGroups(menuGroupIds);
        Objects.requireNonNull(selectMenuResponseDto).setOptionGroups(optionGroupIds);

        return Optional.of(selectMenuResponseDto);
    }

    /**
     * 매장 메뉴 리스트 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장의 메뉴 리스트
     */
    @Override
    public List<SelectMenuResponseDto> lookupMenus(Long storeId) {
        QMenu menu = QMenu.menu;
        QMenuStatus menuStatus = QMenuStatus.menuStatus;
        QStore store = QStore.store;
        QImage image = QImage.image;
        QMenuHasMenuGroup menuHasMenuGroup = QMenuHasMenuGroup.menuHasMenuGroup;
        QMenuHasOptionGroup menuHasOptionGroup = QMenuHasOptionGroup.menuHasOptionGroup;

        List<SelectMenuResponseDto> selectMenuResponseDtoList = jpaQueryFactory
            .select(new QSelectMenuResponseDto(
                menu.id, menuStatus.code, store.id,
                menu.name, menu.price, menu.description,
                menu.image.savedName, menu.cookingTime, menu.earningRate, menu.image.locationType, menu.image.domainName))
            .from(menu)
            .innerJoin(menu.menuStatus, menuStatus)
            .innerJoin(menu.store, store)
            .leftJoin(menu.image, image)
            .where(store.id.eq(storeId), menu.menuStatus.code.ne("OUTED"))
            .fetch();

        for (SelectMenuResponseDto selectMenuResponseDto : selectMenuResponseDtoList) {
            Long menuId = selectMenuResponseDto.getId();

            List<Long> menuGroupIds = jpaQueryFactory
                .select(menuHasMenuGroup.menuGroup.id)
                .from(menuHasMenuGroup)
                .where(menuHasMenuGroup.menu.id.eq(menuId))
                .fetch();

            List<Long> optionGroupIds = jpaQueryFactory
                .select(menuHasOptionGroup.optionGroup.id)
                .from(menuHasOptionGroup)
                .where(menuHasOptionGroup.menu.id.eq(menuId))
                .fetch();

            Objects.requireNonNull(selectMenuResponseDto).setMenuGroups(menuGroupIds);
            Objects.requireNonNull(selectMenuResponseDto).setOptionGroups(optionGroupIds);
        }

        return selectMenuResponseDtoList;
    }
}
