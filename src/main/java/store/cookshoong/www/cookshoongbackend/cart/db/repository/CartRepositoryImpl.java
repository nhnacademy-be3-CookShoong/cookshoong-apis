package store.cookshoong.www.cookshoongbackend.cart.db.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.constructor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.entity.QAccount;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.QCart;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.QCartDetail;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.QCartDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartResponseDto;
import store.cookshoong.www.cookshoongbackend.file.entity.QImage;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.QOption;

/**
 * 회원에 대한 장바구니 내역에 대한 QueryDsl.
 *
 * @author jeongjewan
 * @since 2023.07.28
 */
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean hasCartByAccountId(Long accountId) {
        QCart cart = QCart.cart;

        UUID cartId = jpaQueryFactory
            .select(cart.id)
            .from(cart)
            .where(cart.account.id.eq(accountId))
            .fetchOne();

        return cartId != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID findCartId(Long accountId) {
        QCart cart = QCart.cart;

        return jpaQueryFactory
            .select(cart.id)
            .from(cart)
            .where(cart.account.id.eq(accountId))
            .fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CartResponseDto> lookupCartDbList(Long accountId) {
        QAccount account = QAccount.account;
        QCart cart = QCart.cart;
        QCartDetail cartDetail = QCartDetail.cartDetail;
        QMenu menu = QMenu.menu;
        QMenuStatus menuStatus = QMenuStatus.menuStatus;
        QImage image = QImage.image;
        QCartDetailMenuOption cartDetailMenuOption = QCartDetailMenuOption.cartDetailMenuOption;
        QOption option = QOption.option;

        return jpaQueryFactory
            .from(account)
            .innerJoin(cart).on(account.id.eq(cart.account.id))
            .innerJoin(cart.store)
            .innerJoin(cartDetail).on(cart.id.eq(cartDetail.cart.id))
            .innerJoin(menu).on(cartDetail.menu.id.eq(menu.id))
            .innerJoin(menuStatus).on(menu.menuStatus.eq(menuStatus))
            .leftJoin(image).on(menu.image.id.eq(image.id))
            .leftJoin(cartDetailMenuOption).on(cartDetail.id.eq(cartDetailMenuOption.cartDetail.id))
            .leftJoin(cartDetailMenuOption.option, option).on(option.isDeleted.eq(Boolean.FALSE))
            .where(account.id.eq(accountId), menuStatus.code.ne("OUTED"))
            .transform(groupBy(cartDetail.id)
                .list(constructor(
                    CartResponseDto.class,
                    account.id,
                    cart.store.id,
                    cart.store.name,
                    cart.store.deliveryCost,
                    cart.store.minimumOrderPrice,
                    constructor(
                        CartMenuResponseDto.class,
                        menu.id,
                        menu.name,
                        menu.image.savedName,
                        menu.price,
                        cartDetail.createdTimeMillis,
                        cartDetail.count,
                        menu.image.locationType,
                        menu.image.domainName
                    ),
                    list(constructor(
                        CartOptionResponseDto.class,
                        option.id,
                        option.name,
                        option.price
                    )))));
    }
}
