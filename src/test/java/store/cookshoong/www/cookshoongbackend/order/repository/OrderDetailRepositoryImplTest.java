package store.cookshoong.www.cookshoongbackend.order.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({QueryDslConfig.class, TestPersistEntity.class})
class OrderDetailRepositoryImplTest {
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    TestEntityManager em;
    @Autowired
    TestEntity te;
    @Autowired
    TestPersistEntity tpe;

    Order order;

    List<OrderDetail> orderDetails;

    @BeforeEach
    void beforeEach() {
        Account account = tpe.getLevelOneActiveCustomer();
        Store store = tpe.getOpenStore();
        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        order = te.getOrder(account, store, orderStatus);

        MenuStatus menuStatus = em.persist(new MenuStatus("OPEN", "판매중"));

        Image firstImage = te.getImage("이미지", "", "", true);
        Image secondImage = te.getImage("이미지", "", "",true);
        Image thirdImage = te.getImage("이미지", "", "",true);

        Menu firstMenu = te.getMenu(menuStatus, store, firstImage, new BigDecimal("1.5"));
        Menu secondMenu = te.getMenu(menuStatus, store, secondImage, new BigDecimal("2.0"));
        Menu thirdMenu = te.getMenu(menuStatus, store, thirdImage, null);

        OrderDetail firstOrderDetail = new OrderDetail(order, firstMenu, 1, "메뉴", 2_000);
        OrderDetail secondOrderDetail = new OrderDetail(order, secondMenu, 2, "메뉴", 2_000);
        OrderDetail thirdOrderDetail = new OrderDetail(order, thirdMenu, 3, "메뉴", 2_000);

        orderDetailRepository.save(firstOrderDetail);
        orderDetailRepository.save(secondOrderDetail);
        orderDetailRepository.save(thirdOrderDetail);

        orderDetails = List.of(firstOrderDetail, secondOrderDetail, thirdOrderDetail);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("주문에 대한 메뉴 금액, 개수, 적립률 획득")
    void lookupOrderDetailForPointTest() throws Exception {
        List<LookupOrderDetailResponseDto> lookupOrderDetailResponses = orderDetailRepository.lookupOrderDetailForPoint(order.getCode());

        assertThat(lookupOrderDetailResponses).hasSize(orderDetails.size());

        Iterator<LookupOrderDetailResponseDto> dtoIterator = lookupOrderDetailResponses.iterator();
        Iterator<OrderDetail> iterator = orderDetails.iterator();

        while (iterator.hasNext()) {
            LookupOrderDetailResponseDto dtoNext = dtoIterator.next();
            OrderDetail orderDetail = iterator.next();

            assertThat(orderDetail.getCount()).isEqualTo(dtoNext.getCount());
            assertThat(orderDetail.getNowCost()).isEqualTo(dtoNext.getNowCost());
            BigDecimal earningRate = orderDetail.getMenu().getEarningRate();

            if (Objects.isNull(earningRate)) {
                assertThat(dtoNext.getEarningRate()).isNull();
                continue;
            }

            assertThat(earningRate.doubleValue()).isEqualTo(dtoNext.getEarningRate());
        }

    }

}
