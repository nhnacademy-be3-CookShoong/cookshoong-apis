package store.cookshoong.www.cookshoongbackend.menu_order.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.file.entity.QImage;
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

//        return Optional.ofNullable(jpaQueryFactory
//            .select(new QSelectMenuResponseDto(
//                menu.id, menuStatus.menuStatusCode, store.id,
//                menu.name, menu.price, menu.description,
//                menu.image, menu.cookingTime, menu.earningRate))
//            .from(menu)
//            .innerJoin(menu.menuStatusCode, menuStatus)
//            .innerJoin(menu.store, store)
//            .innerJoin(menu.image, image)
//            .where(menu.id.eq(menuId))
//            .fetchOne());
        return null;
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

//        return jpaQueryFactory
//            .select(new QSelectMenuResponseDto(
//                menu.id, menuStatus.menuStatusCode, store.id,
//                menu.name, menu.price, menu.description,
//                menu.image, menu.cookingTime, menu.earningRate))
//            .from(menu)
//            .innerJoin(menu.menuStatusCode, menuStatus)
//            .innerJoin(menu.store, store)
//            .innerJoin(menu.image, image)
//            .where(store.id.eq(storeId))
//            .fetch();
        return null;
    }
}
