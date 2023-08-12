package store.cookshoong.www.cookshoongbackend.cart.db.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.Cart;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetail;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartResponseDto;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * DB 장바구니에 대한 Repository 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.29
 */
@Slf4j
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class})
class CartRepositoryImplTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    TestEntity tm;

    Store store;
    Account account;
    Merchant merchant;
    Image businessImage;
    Image storeImage;
    Image menuImage;
    MenuStatus menuStatus;
    Menu menu;
    MenuGroup menuGroup;
    MenuHasMenuGroup menuHasMenuGroup;
    OptionGroup optionGroup;
    Option option;
    Cart cart;
    CartDetail cartDetail;
    CartDetailMenuOption cartDetailMenuOption;

    @BeforeEach
    void setUp() {
        AccountStatus accountStatus = tm.getAccountStatusActive();
        Authority authority = new Authority("BUSINESS", "사업자회원");
        Rank rank = tm.getRankLevelOne();

        account = tm.getAccount(accountStatus, authority, rank);
        merchant = tm.getMerchant();

        StoreStatus storeStatus = tm.getStoreStatusOpen();
        BankType bankType = tm.getBankTypeKb();
        businessImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.BUSINESS_INFO_IMAGE.getVariable(), "사업자등록증.png", false);
        storeImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.STORE_IMAGE.getVariable(), "매장사진.png", true);
        store = tm.getStore(merchant, account, bankType, storeStatus, businessImage, storeImage);

        em.persist(accountStatus);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);
        em.persist(merchant);
        em.persist(storeStatus);
        em.persist(bankType);
        em.persist(businessImage);
        em.persist(storeImage);
        em.persist(store);

        menuImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable(), "메뉴사진.png", true);
        menuStatus = ReflectionUtils.newInstance(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");
        ReflectionTestUtils.setField(menuStatus, "description", "판매중");

        menu = new Menu(menuStatus, store, "후라이드 치킨", 19000, "넘버원 바삭바삭", menuImage, 20, BigDecimal.ONE);
        menuGroup = new MenuGroup(store, "후라이드", "바삭바삭", 0);
        menuHasMenuGroup = ReflectionUtils.newInstance(MenuHasMenuGroup.class);
        ReflectionTestUtils.setField(menuHasMenuGroup, "menu", menu);
        ReflectionTestUtils.setField(menuHasMenuGroup, "menuGroup", menuGroup);
        ReflectionTestUtils.setField(menuHasMenuGroup, "menuSequence", 1);
        optionGroup = new OptionGroup(store, "매운맛", null, null, Boolean.FALSE);

        option = new Option(optionGroup, "매운 매문맛", 2000, 0, Boolean.FALSE);
        Option option2 = new Option(optionGroup, "매운 보통", 2000, 0, Boolean.FALSE);
        Option option3 = new Option(optionGroup, "매운 착한", 2000, 0, Boolean.FALSE);

        List<Option> optionList = new ArrayList<>();
        optionList.add(option);
        optionList.add(option2);
        optionList.add(option3);

        em.persist(menuImage);
        em.persist(menuStatus);
        em.persist(menu);
        em.persist(menuGroup);
        em.persist(optionGroup);
        em.persist(optionList.get(0));
        em.persist(optionList.get(1));
        em.persist(optionList.get(2));

        cart = ReflectionUtils.newInstance(Cart.class);
        ReflectionTestUtils.setField(cart, "account", account);
        ReflectionTestUtils.setField(cart, "store", store);

        cartRepository.save(cart);

        cartDetail = new CartDetail(cart, menu, 1, System.currentTimeMillis());
        em.persist(cartDetail);

        for (Option option1 : optionList) {
            cartDetailMenuOption =
                new CartDetailMenuOption(
                    new CartDetailMenuOption.Pk(cartDetail.getId(), option.getId()), cartDetail, option1);
            em.persist(cartDetailMenuOption);
        }
    }

    @Test
    @DisplayName("DB 장바구니에 담긴 장바구니 정보 조회")
    void cartDbList() {

        List<CartResponseDto> carts = cartRepository.lookupCartDbList(account.getId());
        log.info("CARTS: {}", carts);

        assertNotNull(carts);
        assertEquals(carts.get(0).getAccountId(), account.getId());
        assertEquals(carts.get(0).getStoreId(), store.getId());
        assertEquals(carts.get(0).getName(), store.getName());
        assertEquals(carts.get(0).getDeliveryCost(), store.getDeliveryCost());
        assertEquals(carts.get(0).getMinimumOrderPrice(), store.getMinimumOrderPrice());
        assertEquals(carts.get(0).getCartMenuResponseDto().getMenuId(), menu.getId());
        assertEquals(carts.get(0).getCartMenuResponseDto().getPrice(), menu.getPrice());
        assertEquals(carts.get(0).getCartMenuResponseDto().getSavedName(), menuImage.getSavedName());
        assertEquals(carts.get(0).getCartMenuResponseDto().getCreateTimeMillis(), cartDetail.getCreatedTimeMillis());
        assertEquals(carts.get(0).getCartMenuResponseDto().getCount(), cartDetail.getCount());
        assertEquals(carts.get(0).getCartOptionResponseDto().size(), 3);
    }

    @Test
    @DisplayName("장바구니가 존재하는지 여부확인")
    void hasCartByAccountId() {

        boolean actual = cartRepository.hasCartByAccountId(account.getId());

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("DB 장바구니 ID 값 찾아오기")
    void findCartId() {

        UUID cartId = cartRepository.findCartId(account.getId());

        assertNotNull(cartId);
        assertEquals(cartId, cart.getId());
    }
}
