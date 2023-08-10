package store.cookshoong.www.cookshoongbackend.order.repository;

import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInProgressDto;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({QueryDslConfig.class, TestPersistEntity.class})
class OrderRepositoryImplTest {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    TestEntityManager em;
    @Autowired
    TestEntity te;
    @Autowired
    TestPersistEntity tpe;

    Store store;

    @BeforeEach
    void beforeEach() {
        Account account = tpe.getLevelOneActiveCustomer();
        store = tpe.getOpenStore();
        OrderStatus orderStatus = te.getOrderStatus("PAY", "생성");
        Order firstOrder = te.getOrder(account, store, orderStatus);
        Order secondOrder = te.getOrder(account, store, orderStatus);
        Order thirdOrder = te.getOrder(account, store, orderStatus);

        MenuStatus menuStatus = em.persist(new MenuStatus("OPEN", "판매중"));

        Image firstImage = te.getImage("이미지", "", "", true);
        Image secondImage = te.getImage("이미지", "", "",true);
        Image thirdImage = te.getImage("이미지", "", "",true);

        Menu firstMenu = te.getMenu(menuStatus, store, firstImage, new BigDecimal("1.5"));
        Menu secondMenu = te.getMenu(menuStatus, store, secondImage, new BigDecimal("2.0"));
        Menu thirdMenu = te.getMenu(menuStatus, store, thirdImage, null);

        OrderDetail firstOrderFirstDetail = te.getOrderDetail(firstOrder, firstMenu, 1);
        OrderDetail firstOrderSecondDetail = te.getOrderDetail(firstOrder, secondMenu, 2);
        OrderDetail firstOrderThirdDetail = te.getOrderDetail(firstOrder, thirdMenu, 3);

        OrderDetail secondOrderFirstDetail = te.getOrderDetail(secondOrder, firstMenu, 1);
        OrderDetail secondOrderSecondDetail = te.getOrderDetail(secondOrder, secondMenu, 2);

        OrderDetail thirdOrderFirstDetail = te.getOrderDetail(thirdOrder, firstMenu, 1);

        OptionGroup optionGroup = te.getOptionGroup(store);

        Option firstOption = te.getOption(optionGroup);
        Option secondOption = te.getOption(optionGroup);
        Option thirdOption = te.getOption(optionGroup);

        te.getOrderDetailMenuOption(firstOption, firstOrderFirstDetail);
        te.getOrderDetailMenuOption(secondOption, firstOrderFirstDetail);
        te.getOrderDetailMenuOption(thirdOption, firstOrderFirstDetail);
        te.getOrderDetailMenuOption(secondOption, secondOrderFirstDetail);

        ChargeType chargeType = te.getChargeType();
        te.getCharge(chargeType, firstOrder);
        te.getCharge(chargeType, secondOrder);
        te.getCharge(chargeType, thirdOrder);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("주문 메뉴 목록 확인 테스트")
    void selectOrderInProgressTest() throws Exception {
        List<LookupOrderInProgressDto> selectOrderInProgresses =
            orderRepository.lookupOrderInStatus(store, getStatusCodeString(Set.of(PAY, COOKING)));
        System.out.println();
    }
}
