package store.cookshoong.www.cookshoongbackend.order.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupAccountOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
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

    Order firstOrder;
    Order secondOrder;
    Order thirdOrder;

    OrderDetail firstOrderFirstDetail;
    OrderDetail firstOrderSecondDetail;
    OrderDetail firstOrderThirdDetail;
    OrderDetail secondOrderFirstDetail;
    OrderDetail secondOrderSecondDetail;
    OrderDetail thirdOrderFirstDetail;

    OrderDetailMenuOption firstOrderFirstDetailFirstOption;
    OrderDetailMenuOption firstOrderFirstDetailSecondOption;
    OrderDetailMenuOption firstOrderFirstDetailThirdOption;
    OrderDetailMenuOption secondOrderFirstDetailFirstOption;

    Account account;

    @BeforeEach
    void beforeEach() {
        account = tpe.getLevelOneActiveCustomer();
        store = tpe.getOpenStore();
        OrderStatus firstOrderStatus = te.getOrderStatus("CREATE", "첫번째 주문 상태");
        OrderStatus secondOrderStatus = te.getOrderStatus("PAY", "두번째 주문 상태");
        OrderStatus thirdOrderStatus = te.getOrderStatus("CANCEL", "세번째 주문 상태");
        firstOrder = te.getOrder(account, store, firstOrderStatus);
        secondOrder = te.getOrder(account, store, secondOrderStatus);
        thirdOrder = te.getOrder(account, store, thirdOrderStatus);

        MenuStatus menuStatus = em.persist(new MenuStatus("OPEN", "판매중"));

        Image firstImage = te.getImage("첫번째 이미지", "", "", true);
        Image secondImage = te.getImage("두번째 이미지", "", "",true);
        Image thirdImage = te.getImage("세번째 이미지", "", "",true);

        Menu firstMenu = te.getMenu(menuStatus, store, firstImage, new BigDecimal("1.5"));
        Menu secondMenu = te.getMenu(menuStatus, store, secondImage, new BigDecimal("2.0"));
        Menu thirdMenu = te.getMenu(menuStatus, store, thirdImage, null);

        firstOrderFirstDetail = te.getOrderDetail(firstOrder, firstMenu, 1);
        firstOrderSecondDetail = te.getOrderDetail(firstOrder, secondMenu, 2);
        firstOrderThirdDetail = te.getOrderDetail(firstOrder, thirdMenu, 3);

        secondOrderFirstDetail = te.getOrderDetail(secondOrder, firstMenu, 1);
        secondOrderSecondDetail = te.getOrderDetail(secondOrder, secondMenu, 2);

        thirdOrderFirstDetail = te.getOrderDetail(thirdOrder, firstMenu, 1);

        OptionGroup optionGroup = te.getOptionGroup(store);

        Option firstOption = em.persist(new Option(optionGroup, "첫번째 옵션", 500, 1, false));
        Option secondOption = em.persist(new Option(optionGroup, "두번째 옵션", 1_500, 2, false));
        Option thirdOption = em.persist(new Option(optionGroup, "세번째 옵션", 2_500, 3, false));

        firstOrderFirstDetailFirstOption = te.getOrderDetailMenuOption(firstOption, firstOrderFirstDetail);
        firstOrderFirstDetailSecondOption = te.getOrderDetailMenuOption(secondOption, firstOrderFirstDetail);
        firstOrderFirstDetailThirdOption = te.getOrderDetailMenuOption(thirdOption, firstOrderFirstDetail);
        secondOrderFirstDetailFirstOption = te.getOrderDetailMenuOption(secondOption, secondOrderFirstDetail);

        ChargeType chargeType = te.getChargeType();
        te.getCharge(chargeType, firstOrder);
        te.getCharge(chargeType, secondOrder);
        te.getCharge(chargeType, thirdOrder);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("주문 메뉴 상세 확인 테스트")
    void lookupOrderInStatusOrderDetailTest() throws Exception {
        List<LookupOrderInStatusResponseDto> lookupOrdersInStatus =
            orderRepository.lookupOrderInStatus(store, getStatusCodeString(Set.of(CREATE, PAY, CANCEL)));

        assertThat(lookupOrdersInStatus).hasSize(3);

        Map<UUID, Set<OrderDetail>> verifies = Map.of(
            firstOrder.getCode(), Set.of(firstOrderFirstDetail, firstOrderSecondDetail, firstOrderThirdDetail),
            secondOrder.getCode(), Set.of(secondOrderFirstDetail, secondOrderSecondDetail),
            thirdOrder.getCode(), Set.of(thirdOrderFirstDetail)
        );

        for (LookupOrderInStatusResponseDto lookupOrderInStatus : lookupOrdersInStatus) {
            Set<Long> orderDetails = verifies.get(lookupOrderInStatus.getOrderCode())
                .stream()
                .map(OrderDetail::getId)
                .collect(Collectors.toSet());

            List<LookupOrderDetailMenuResponseDto> lookupOrderDetails = lookupOrderInStatus.getSelectOrderDetails();
            assertThat(lookupOrderDetails).hasSize(orderDetails.size());

            for (LookupOrderDetailMenuResponseDto lookupOrderDetail : lookupOrderDetails) {
                assertThat(orderDetails).contains(lookupOrderDetail.getOrderDetailId());
            }
        }
    }

    @Test
    @DisplayName("주문 메뉴 상세 옵션 확인 테스트")
    void lookupOrderInStatusOrderDetailMenuOptionsTest() throws Exception {
        List<LookupOrderInStatusResponseDto> lookupOrdersInStatus =
            orderRepository.lookupOrderInStatus(store, getStatusCodeString(Set.of(CREATE, PAY, CANCEL)));

        assertThat(lookupOrdersInStatus).hasSize(3);

        Map<Long, Set<OrderDetailMenuOption>> verifies = Map.of(
            firstOrderFirstDetail.getId(), Set.of(
                firstOrderFirstDetailFirstOption, firstOrderFirstDetailSecondOption, firstOrderFirstDetailThirdOption),
            secondOrderFirstDetail.getId(), Set.of(secondOrderFirstDetailFirstOption)
        );

        List<LookupOrderDetailMenuResponseDto> orderDetails = lookupOrdersInStatus.stream()
            .flatMap(o -> o.getSelectOrderDetails().stream())
            .collect(Collectors.toList());

        for (LookupOrderDetailMenuResponseDto orderDetail : orderDetails) {
            Set<OrderDetailMenuOption> options =
                verifies.getOrDefault(orderDetail.getOrderDetailId(), Collections.emptySet());

            assertThat(orderDetail.getSelectOrderDetailMenuOptions()).hasSize(options.size());

            Set<String> optionNames = options.stream()
                .map(option -> option.getOption().getName())
                .collect(Collectors.toSet());

            List<String> detailOptionNames = orderDetail.getSelectOrderDetailMenuOptions().stream()
                .map(LookupOrderDetailMenuOptionResponseDto::getOptionName)
                .collect(Collectors.toList());

            assertThat(detailOptionNames).hasSize(optionNames.size());

            for (String detailOptionName : detailOptionNames) {
                assertThat(optionNames).contains(detailOptionName);
            }
        }
    }

    @Test
    @DisplayName("주문 메뉴 상세 확인 페이지 테스트")
    void lookupOrderInStatusPageOrderDetailTest() throws Exception {
        Page<LookupOrderInStatusResponseDto> lookupOrdersInStatus =
            orderRepository.lookupOrderInStatus(store, getStatusCodeString(Set.of(CREATE, PAY, CANCEL)), Pageable.ofSize(2));

        assertThat(lookupOrdersInStatus.getTotalElements()).isEqualTo(3);
        assertThat(lookupOrdersInStatus).hasSize(2);

        Map<UUID, Set<OrderDetail>> verifies = Map.of(
            firstOrder.getCode(), Set.of(firstOrderFirstDetail, firstOrderSecondDetail, firstOrderThirdDetail),
            secondOrder.getCode(), Set.of(secondOrderFirstDetail, secondOrderSecondDetail),
            thirdOrder.getCode(), Set.of(thirdOrderFirstDetail)
        );

        for (LookupOrderInStatusResponseDto lookupOrderInStatus : lookupOrdersInStatus) {
            Set<Long> orderDetails = verifies.get(lookupOrderInStatus.getOrderCode())
                .stream()
                .map(OrderDetail::getId)
                .collect(Collectors.toSet());

            List<LookupOrderDetailMenuResponseDto> lookupOrderDetails = lookupOrderInStatus.getSelectOrderDetails();
            assertThat(lookupOrderDetails).hasSize(orderDetails.size());

            for (LookupOrderDetailMenuResponseDto lookupOrderDetail : lookupOrderDetails) {
                assertThat(orderDetails).contains(lookupOrderDetail.getOrderDetailId());
            }
        }
    }

    @Test
    @DisplayName("주문 메뉴 상세 옵션 확인 페이지 테스트")
    void lookupOrderInStatusPageOrderDetailMenuOptionsTest() throws Exception {
        Page<LookupOrderInStatusResponseDto> lookupOrdersInStatus =
            orderRepository.lookupOrderInStatus(store, getStatusCodeString(Set.of(CREATE, PAY, CANCEL)), Pageable.ofSize(2));

        assertThat(lookupOrdersInStatus.getTotalElements()).isEqualTo(3);
        assertThat(lookupOrdersInStatus).hasSize(2);

        Map<Long, Set<OrderDetailMenuOption>> verifies = Map.of(
            firstOrderFirstDetail.getId(), Set.of(
                firstOrderFirstDetailFirstOption, firstOrderFirstDetailSecondOption, firstOrderFirstDetailThirdOption),
            secondOrderFirstDetail.getId(), Set.of(secondOrderFirstDetailFirstOption)
        );

        List<LookupOrderDetailMenuResponseDto> orderDetails = lookupOrdersInStatus.stream()
            .flatMap(o -> o.getSelectOrderDetails().stream())
            .collect(Collectors.toList());

        for (LookupOrderDetailMenuResponseDto orderDetail : orderDetails) {
            Set<OrderDetailMenuOption> options =
                verifies.getOrDefault(orderDetail.getOrderDetailId(), Collections.emptySet());

            assertThat(orderDetail.getSelectOrderDetailMenuOptions()).hasSize(options.size());

            Set<String> optionNames = options.stream()
                .map(option -> option.getOption().getName())
                .collect(Collectors.toSet());

            List<String> detailOptionNames = orderDetail.getSelectOrderDetailMenuOptions().stream()
                .map(LookupOrderDetailMenuOptionResponseDto::getOptionName)
                .collect(Collectors.toList());

            assertThat(detailOptionNames).hasSize(optionNames.size());

            for (String detailOptionName : detailOptionNames) {
                assertThat(optionNames).contains(detailOptionName);
            }
        }
    }

    @Test
    @DisplayName("사용자 주문 메뉴 상세 확인 테스트")
    void lookupAccountOrderInStatusOrderDetailTest() throws Exception {
        Page<LookupAccountOrderInStatusResponseDto> lookupAccountOrdersInStatus =
            orderRepository.lookupOrderInStatus(account, getStatusCodeString(Set.of(CREATE, PAY, CANCEL)), Pageable.ofSize(2));

        assertThat(lookupAccountOrdersInStatus.getTotalElements()).isEqualTo(3);
        assertThat(lookupAccountOrdersInStatus).hasSize(2);

        Map<UUID, Set<OrderDetail>> verifies = Map.of(
            firstOrder.getCode(), Set.of(firstOrderFirstDetail, firstOrderSecondDetail, firstOrderThirdDetail),
            secondOrder.getCode(), Set.of(secondOrderFirstDetail, secondOrderSecondDetail),
            thirdOrder.getCode(), Set.of(thirdOrderFirstDetail)
        );

        List<LookupOrderInStatusResponseDto> lookupOrdersInStatus = lookupAccountOrdersInStatus.stream()
            .map(LookupAccountOrderInStatusResponseDto::getLookupOrderInStatusResponseDto)
            .collect(Collectors.toList());

        for (LookupOrderInStatusResponseDto lookupOrderInStatus : lookupOrdersInStatus) {
            Set<Long> orderDetails = verifies.get(lookupOrderInStatus.getOrderCode())
                .stream()
                .map(OrderDetail::getId)
                .collect(Collectors.toSet());

            List<LookupOrderDetailMenuResponseDto> lookupOrderDetails = lookupOrderInStatus.getSelectOrderDetails();
            assertThat(lookupOrderDetails).hasSize(orderDetails.size());

            for (LookupOrderDetailMenuResponseDto lookupOrderDetail : lookupOrderDetails) {
                assertThat(orderDetails).contains(lookupOrderDetail.getOrderDetailId());
            }
        }
    }

    @Test
    @DisplayName("주문 메뉴 상세 옵션 확인 페이지 테스트")
    void lookupAccountOrderInStatusOrderDetailMenuOptionsTest() throws Exception {
        Page<LookupAccountOrderInStatusResponseDto> lookupAccountOrdersInStatus =
            orderRepository.lookupOrderInStatus(account, getStatusCodeString(Set.of(CREATE, PAY, CANCEL)), Pageable.ofSize(2));

        assertThat(lookupAccountOrdersInStatus.getTotalElements()).isEqualTo(3);
        assertThat(lookupAccountOrdersInStatus).hasSize(2);

        Map<Long, Set<OrderDetailMenuOption>> verifies = Map.of(
            firstOrderFirstDetail.getId(), Set.of(
                firstOrderFirstDetailFirstOption, firstOrderFirstDetailSecondOption, firstOrderFirstDetailThirdOption),
            secondOrderFirstDetail.getId(), Set.of(secondOrderFirstDetailFirstOption)
        );

        List<LookupOrderInStatusResponseDto> lookupOrdersInStatus = lookupAccountOrdersInStatus.stream()
            .map(LookupAccountOrderInStatusResponseDto::getLookupOrderInStatusResponseDto)
            .collect(Collectors.toList());

        List<LookupOrderDetailMenuResponseDto> orderDetails = lookupOrdersInStatus.stream()
            .flatMap(o -> o.getSelectOrderDetails().stream())
            .collect(Collectors.toList());

        for (LookupOrderDetailMenuResponseDto orderDetail : orderDetails) {
            Set<OrderDetailMenuOption> options =
                verifies.getOrDefault(orderDetail.getOrderDetailId(), Collections.emptySet());

            assertThat(orderDetail.getSelectOrderDetailMenuOptions()).hasSize(options.size());

            Set<String> optionNames = options.stream()
                .map(option -> option.getOption().getName())
                .collect(Collectors.toSet());

            List<String> detailOptionNames = orderDetail.getSelectOrderDetailMenuOptions().stream()
                .map(LookupOrderDetailMenuOptionResponseDto::getOptionName)
                .collect(Collectors.toList());

            assertThat(detailOptionNames).hasSize(optionNames.size());

            for (String detailOptionName : detailOptionNames) {
                assertThat(optionNames).contains(detailOptionName);
            }
        }
    }
}
