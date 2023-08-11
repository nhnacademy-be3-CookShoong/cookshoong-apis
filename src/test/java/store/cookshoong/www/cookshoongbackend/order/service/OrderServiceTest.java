package store.cookshoong.www.cookshoongbackend.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
import store.cookshoong.www.cookshoongbackend.order.exception.OrderStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.exception.PriceIncreaseException;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailMenuOptionRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderStatusRepository;
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
            new CartRedisDto(accountId, storeId, "주호타코",
                new CartMenuDto(1L, "타코야끼", null, 4_000, LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable()),
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

}
