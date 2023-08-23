package store.cookshoong.www.cookshoongbackend.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotInStoreException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotOpenException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.exception.PriceIncreaseException;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupAccountOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailMenuOptionRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderStatusRepository;
import store.cookshoong.www.cookshoongbackend.point.model.event.PointOrderCompleteEvent;
import store.cookshoong.www.cookshoongbackend.point.service.PointEventService;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotOpenException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderStatusRepository orderStatusRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    StoreRepository storeRepository;
    @Mock
    OrderDetailRepository orderDetailRepository;
    @Mock
    OrderDetailMenuOptionRepository orderDetailMenuOptionRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OptionRepository optionRepository;
    @Spy
    ApplicationEventPublisher publisher;
    @Spy
    TestEntity te;
    @InjectMocks
    TestPersistEntity tpe;

    CreateOrderRequestDto createOrderRequestDto;

    List<CartRedisDto> cartItems;

    @BeforeEach
    void beforeEach() {
        Long accountId = Long.MIN_VALUE;
        Long storeId = Long.MIN_VALUE + 1L;

        createOrderRequestDto = te.createUsingDeclared(CreateOrderRequestDto.class);
        ReflectionTestUtils.setField(createOrderRequestDto, "orderCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createOrderRequestDto, "accountId", accountId);
        ReflectionTestUtils.setField(createOrderRequestDto, "storeId", storeId);
        ReflectionTestUtils.setField(createOrderRequestDto, "memo", "요청사항");
        ReflectionTestUtils.setField(createOrderRequestDto, "issueCouponCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createOrderRequestDto, "pointAmount", 100);

        cartItems = List.of(
            new CartRedisDto(accountId, storeId, "주호타코", 3000, 10000,
                new CartMenuDto(1L, "타코야끼", null, 4_000),
                List.of(new CartOptionDto(2L, "가쓰오부시 추가", 500)),
                100L, "hashKey", 2, "타코야끼 : 가쓰오부시 추가",
                "9_000"));


    }

    @Test
    @DisplayName("주문 실패 - 사용자 정보 없음")
    void orderAccountNotFoundFailTest() throws Exception {
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 주문 상태 없음")
    void orderStatusNotFoundFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderStatusNotFoundException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 매장 없음")
    void orderStoreNotFoundFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(StoreNotFoundException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 매장 영업중이 아님")
    void orderStoreNotOpenFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = tpe.getOpenStore();
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        StoreStatus storeStatus = te.createUsingDeclared(StoreStatus.class);
        ReflectionTestUtils.setField(storeStatus, "code", "CLOSE");
        ReflectionTestUtils.setField(openStore, "storeStatus", storeStatus);

        assertThrowsExactly(StoreNotOpenException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 메뉴 없음")
    void orderMenuNotFoundFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = tpe.getOpenStore();
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(MenuNotFoundException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 메뉴 판매중이 아님")
    void orderMenuNotOpenFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = tpe.getOpenStore();
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatus menuStatus = te.createUsingDeclared(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "CLOSE");

        Menu menu = new Menu(menuStatus, openStore, "타코야끼", 2_000, "", null,
            100, new BigDecimal("1.0"));
        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.of(menu));

        assertThrowsExactly(MenuNotOpenException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 메뉴 가격 상승")
    void orderMenuIncreasePriceFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = tpe.getOpenStore();
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatus menuStatus = te.createUsingDeclared(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");

        Menu menu = new Menu(menuStatus, openStore, "타코야끼", 4_500, "", null,
            100, new BigDecimal("1.0"));
        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.of(menu));

        assertThrowsExactly(PriceIncreaseException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 주문한 가게의 메뉴가 아님")
    void orderNotStoreMenuFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = te.persist(tpe.getOpenStore());
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatus menuStatus = te.createUsingDeclared(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");

        Menu menu = new Menu(menuStatus, te.persist(tpe.getOpenStore()), "타코야끼", 4_000, "",
            null, 100, new BigDecimal("1.0"));
        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.of(menu));

        assertThrowsExactly(MenuNotInStoreException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 옵션 없음")
    void orderOptionNotFoundFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = te.persist(tpe.getOpenStore());
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatus menuStatus = te.createUsingDeclared(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");

        Menu menu = new Menu(menuStatus, openStore, "타코야끼", 4_000, "",
            null, 100, new BigDecimal("1.0"));
        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.of(menu));

        when(orderDetailRepository.save(any(OrderDetail.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        when(optionRepository.findByIdAndIsDeletedFalse(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OptionNotFoundException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 실패 - 옵션 가격 상승")
    void orderOptionIncreasePriceFailTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = te.persist(tpe.getOpenStore());
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatus menuStatus = te.createUsingDeclared(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");

        Menu menu = new Menu(menuStatus, openStore, "타코야끼", 4_000, "",
            null, 100, new BigDecimal("1.0"));
        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.of(menu));

        when(orderDetailRepository.save(any(OrderDetail.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Option option = new Option(
            new OptionGroup(openStore, "추가", 0, 1, false),
            "가쓰오부시 추가", 600, 1, false);
        when(optionRepository.findByIdAndIsDeletedFalse(anyLong()))
            .thenReturn(Optional.of(option));

        assertThrowsExactly(PriceIncreaseException.class,
            () -> orderService.createOrder(createOrderRequestDto, cartItems));
    }

    @Test
    @DisplayName("주문 성공")
    void orderSuccessTest() throws Exception {
        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        OrderStatus orderStatus = te.getOrderStatus("CREATE", "생성");
        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.of(orderStatus));

        Store openStore = te.persist(tpe.getOpenStore());
        when(storeRepository.findById(anyLong()))
            .thenReturn(Optional.of(openStore));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        MenuStatus menuStatus = te.createUsingDeclared(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");

        Menu menu = new Menu(menuStatus, openStore, "타코야끼", 4_000, "",
            null, 100, new BigDecimal("1.0"));
        when(menuRepository.findById(anyLong()))
            .thenReturn(Optional.of(menu));

        when(orderDetailRepository.save(any(OrderDetail.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Option option = new Option(
            new OptionGroup(openStore, "추가", 0, 1, false),
            "가쓰오부시 추가", 500, 1, false);
        when(optionRepository.findByIdAndIsDeletedFalse(anyLong()))
            .thenReturn(Optional.of(option));

        assertDoesNotThrow(() -> orderService.createOrder(createOrderRequestDto, cartItems));
        verify(orderDetailMenuOptionRepository).save(any(OrderDetailMenuOption.class));
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 주문 없음")
    void changeStatusOrderNotFoundTest() throws Exception {
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class, () ->
            orderService.changeStatus(UUID.randomUUID(), OrderStatus.StatusCode.COMPLETE));
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 주문 상태 없음")
    void changeStatusOrderStatusNotFoundTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        when(orderStatusRepository.findByOrderStatusCode(any(OrderStatus.StatusCode.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderStatusNotFoundException.class, () ->
            orderService.changeStatus(UUID.randomUUID(), OrderStatus.StatusCode.COMPLETE));
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.StatusCode.class)
    @DisplayName("주문 상태 변경 성공")
    void changeStatusSuccessTest(OrderStatus.StatusCode statusCode) throws Exception {
        Order order = spy(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        OrderStatus orderStatus = mock(OrderStatus.class);
        when(orderStatusRepository.findByOrderStatusCode(statusCode))
            .thenReturn(Optional.of(orderStatus));

        UUID uuid = UUID.randomUUID();

        if (statusCode.equals(OrderStatus.StatusCode.COMPLETE)) {
            doNothing().when(publisher)
                .publishEvent(any(PointOrderCompleteEvent.class));
        }

        assertDoesNotThrow(() ->
            orderService.changeStatus(uuid, statusCode));

        verify(order).updateOrderStatus(orderStatus);
    }

    @Test
    @DisplayName("진행중인 주문 확인 실패 - 매장 없음")
    void lookupOrderInProgressStoreNotFoundFailTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        when(storeRepository.findById(storeId))
            .thenReturn(Optional.empty());

        assertThrowsExactly(StoreNotFoundException.class, () ->
            orderService.lookupOrderInProgress(storeId));
    }

    @Test
    @DisplayName("진행중인 주문 확인 성공")
    void lookupOrderInProgressStoreSuccessTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId))
            .thenReturn(Optional.of(store));

        List<LookupOrderInStatusResponseDto> responses = new ArrayList<>();
        when(orderRepository.lookupOrderInStatus(any(Store.class), anySet()))
            .thenReturn(responses);

        assertThat(orderService.lookupOrderInProgress(storeId))
            .isEqualTo(responses);
    }

    @Test
    @DisplayName("완료된 주문 확인 실패 - 매장 없음")
    void lookupOrderInCompleteStoreNotFoundFailTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        when(storeRepository.findById(storeId))
            .thenReturn(Optional.empty());

        assertThrowsExactly(StoreNotFoundException.class, () ->
            orderService.lookupOrderInComplete(storeId, Pageable.ofSize(10)));
    }

    @Test
    @DisplayName("완료된 주문 확인 성공")
    void lookupOrderInCompleteStoreSuccessTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId))
            .thenReturn(Optional.of(store));

        Pageable pageable = Pageable.ofSize(10);

        Page<LookupOrderInStatusResponseDto> responses = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(orderRepository.lookupOrderInStatus(any(Store.class), anySet(), any(Pageable.class)))
            .thenReturn(responses);

        assertThat(orderService.lookupOrderInComplete(storeId, pageable))
            .isEqualTo(responses);
    }

    @Test
    @DisplayName("주문 가능 여부 반환 실패 - 매장 없음")
    void selectOrderPossibleStoreNotFoundTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        when(storeRepository.findById(storeId))
            .thenReturn(Optional.empty());

        assertThrowsExactly(StoreNotFoundException.class, () ->
            orderService.selectOrderPossible(true, storeId, 0));
    }

    @Test
    @DisplayName("주문 가능 여부 반환 - 주문할 수 없는 거리")
    void selectOrderPossibleOverDistanceTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId))
            .thenReturn(Optional.of(store));

        when(store.getMinimumOrderPrice())
            .thenReturn(0);

        StoreStatus storeStatus = mock(StoreStatus.class);
        when(store.getStoreStatus())
            .thenReturn(storeStatus);

        when(storeStatus.getCode())
            .thenReturn("OPEN");

        assertThat(orderService.selectOrderPossible(false, storeId, 10_000)
            .isOrderPossible())
            .isFalse();
    }

    @Test
    @DisplayName("주문 가능 여부 반환 - 최소 주문 금액 미충족")
    void selectOrderPossibleUnderMinimumOrderPriceTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId))
            .thenReturn(Optional.of(store));

        when(store.getMinimumOrderPrice())
            .thenReturn(10_000);

        StoreStatus storeStatus = mock(StoreStatus.class);
        when(store.getStoreStatus())
            .thenReturn(storeStatus);

        when(storeStatus.getCode())
            .thenReturn("OPEN");

        assertThat(orderService.selectOrderPossible(true, storeId, 0)
            .isOrderPossible())
            .isFalse();
    }

    @Test
    @DisplayName("주문 가능 여부 반환 - 매장 문 닫음")
    void selectOrderPossibleStoreStatusCloseTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId))
            .thenReturn(Optional.of(store));

        when(store.getMinimumOrderPrice())
            .thenReturn(0);

        StoreStatus storeStatus = mock(StoreStatus.class);
        when(store.getStoreStatus())
            .thenReturn(storeStatus);

        when(storeStatus.getCode())
            .thenReturn("Close");

        assertThat(orderService.selectOrderPossible(true, storeId, 10_000)
            .isOrderPossible())
            .isFalse();
    }

    @Test
    @DisplayName("주문 가능 여부 반환 - 가능")
    void selectOrderPossibleOkTest() throws Exception {
        Long storeId = Long.MIN_VALUE;

        Store store = mock(Store.class);
        when(storeRepository.findById(storeId))
            .thenReturn(Optional.of(store));

        when(store.getMinimumOrderPrice())
            .thenReturn(0);

        StoreStatus storeStatus = mock(StoreStatus.class);
        when(store.getStoreStatus())
            .thenReturn(storeStatus);

        when(storeStatus.getCode())
            .thenReturn("OPEN");

        assertThat(orderService.selectOrderPossible(true, storeId, 10_000)
            .isOrderPossible())
            .isTrue();
    }

    @Test
    @DisplayName("사용자 주문 페이지 확인 실패 - 사용자 찾지 못 함")
    void lookupAccountOrdersAccountNotFoundTest() throws Exception {
        Long accountId = Long.MIN_VALUE;
        when(accountRepository.findById(accountId))
            .thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () ->
            orderService.lookupAccountOrders(accountId, Pageable.ofSize(10)));
    }

    @Test
    @DisplayName("사용자 주문 페이지 확인 성공")
    void lookupAccountOrdersSuccessTest() throws Exception {
        Long accountId = Long.MIN_VALUE;

        Account account = mock(Account.class);
        when(accountRepository.findById(accountId))
            .thenReturn(Optional.of(account));

        Pageable pageable = Pageable.ofSize(10);
        Page<LookupAccountOrderInStatusResponseDto> responses = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(orderRepository.lookupOrderInStatus(any(Account.class), anySet(), any(Pageable.class)))
            .thenReturn(responses);


        assertThat(orderService.lookupAccountOrders(accountId, pageable))
            .isEqualTo(responses);
    }
}
