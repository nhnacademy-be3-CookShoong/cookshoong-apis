package store.cookshoong.www.cookshoongbackend.order.service;

import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.COMPLETE;
import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.COOKING;
import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.DELIVER;
import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.PAY;
import static store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode.getStatusCodeString;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Consumer;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotInStoreException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotOpenException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus.StatusCode;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.exception.PriceIncreaseException;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.SelectOrderPossibleResponseDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailMenuOptionRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderStatusRepository;
import store.cookshoong.www.cookshoongbackend.point.model.event.PointOrderCompleteEvent;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotOpenException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 주문 비즈니스 로직을 담당하는 service.
 *
 * @author eora21 (김주호)
 * @since 2023.08.02
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private static final String STATUS_OPEN = "OPEN";
    private final Map<StatusCode, Consumer<UUID>> statusCodeConsumer = createStatusCodeConsumer();
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMenuOptionRepository orderDetailMenuOptionRepository;
    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;
    private final ApplicationEventPublisher publisher;

    private Map<StatusCode, Consumer<UUID>> createStatusCodeConsumer() {
        Map<StatusCode, Consumer<UUID>> statusCodeFunctions = new EnumMap<>(StatusCode.class);
        statusCodeFunctions.put(COMPLETE, this::statusCompleteEvent);
        statusCodeFunctions.put(DELIVER, this::startDeliverEvent);

        return statusCodeFunctions;
    }

    /**
     * 주문 생성.
     *
     * @param createOrderRequestDto the create order request dto
     * @param cartItems             the cart items
     */
    public void createOrder(CreateOrderRequestDto createOrderRequestDto, List<CartRedisDto> cartItems) {
        Account account = accountRepository.findById(createOrderRequestDto.getAccountId())
            .orElseThrow(UserNotFoundException::new);

        OrderStatus orderStatusCreate = orderStatusRepository.findByOrderStatusCode(StatusCode.CREATE)
            .orElseThrow(OrderStatusNotFoundException::new);

        Store store = storeRepository.findById(createOrderRequestDto.getStoreId())
            .orElseThrow(StoreNotFoundException::new);

        if (!store.getStoreStatus().getCode().equals(STATUS_OPEN)) {
            throw new StoreNotOpenException();
        }

        Order order = orderRepository.save(new Order(createOrderRequestDto.getOrderCode(), orderStatusCreate, account,
            store, createOrderRequestDto.getDeliveryAddress(), createOrderRequestDto.getDeliveryCost(),
            createOrderRequestDto.getMemo()));

        cartItems.forEach(cartMenu -> createOrderDetail(cartMenu, order));
    }

    private void createOrderDetail(CartRedisDto cartMenu, Order order) {
        Store store = order.getStore();

        CartMenuDto menuDto = cartMenu.getMenu();
        Menu menu = menuRepository.findById(menuDto.getMenuId())
            .orElseThrow(MenuNotFoundException::new);

        if (!menu.getMenuStatus().getCode().equals(STATUS_OPEN)) {
            throw new MenuNotOpenException();
        }

        validPriceIncrease(menu.getPrice(), menuDto.getMenuPrice());

        if (!menu.getStore().getId().equals(store.getId())) {
            throw new MenuNotInStoreException();
        }

        OrderDetail orderDetail = orderDetailRepository.save(cartMenu.toEntity(order, menu));

        List<CartOptionDto> options = cartMenu.getOptions();

        if (Objects.isNull(options)) {
            return;
        }

        options.forEach(optionDto -> createOrderDetailMenuOption(optionDto, orderDetail));
    }

    private void createOrderDetailMenuOption(CartOptionDto optionDto,
                                             OrderDetail orderDetail) {
        Option option = optionRepository.findByIdAndIsDeletedFalse(optionDto.getOptionId())
            .orElseThrow(OptionNotFoundException::new);

        validPriceIncrease(option.getPrice(), optionDto.getOptionPrice());

        orderDetailMenuOptionRepository.save(optionDto.toEntity(option, orderDetail));
    }

    private void validPriceIncrease(Integer nowPrice, Integer cartPrice) {
        if (nowPrice.compareTo(cartPrice) > 0) {
            throw new PriceIncreaseException();
        }
    }

    /**
     * 주문 상태 변경.
     *
     * @param orderCode  the order code
     * @param statusCode the status code
     */
    public void changeStatus(@Valid UUID orderCode, StatusCode statusCode) {
        Order order = orderRepository.findById(orderCode)
            .orElseThrow(StoreNotFoundException::new);

        OrderStatus orderStatus = orderStatusRepository.findByOrderStatusCode(statusCode)
            .orElseThrow(OrderStatusNotFoundException::new);

        order.updateOrderStatus(orderStatus);
        statusCodeConsumer.getOrDefault(statusCode, ignore -> {})
            .accept(orderCode);
    }

    /**
     * 진행중인 주문 확인.
     *
     * @param storeId the store id
     * @return the list
     */
    public List<LookupOrderInStatusResponseDto> lookupOrderInProgress(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);

        Set<String> statusCodeString = getStatusCodeString(Set.of(PAY, COOKING));

        return orderRepository.lookupOrderInStatus(store, statusCodeString);
    }

    /**
     * 완료된 주문 확인.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return the page
     */
    public Page<LookupOrderInStatusResponseDto> lookupOrderInComplete(Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        Set<String> statusCodeString = getStatusCodeString(Set.of(COMPLETE));

        return orderRepository.lookupOrderInStatus(store, statusCodeString, pageable);
    }

    private void statusCompleteEvent(UUID orderCode) {
        publisher.publishEvent(new PointOrderCompleteEvent(this, orderCode));
    }

    private void startDeliverEvent(UUID orderCode) {
        // TODO: 배송 API 연결할 것
    }

    /**
     * 주문 가능한 지 여부를 반환.
     *
     * @param inStandardDistance the in standard distance
     * @param storeId            the store id
     * @param totalPrice         the total price
     * @return the select order possible response dto
     */
    @Transactional(readOnly = true)
    public SelectOrderPossibleResponseDto selectOrderPossible(boolean inStandardDistance, Long storeId,
                                                              int totalPrice) {
        boolean response = true;
        StringJoiner stringJoiner = new StringJoiner("\n");

        if (inStandardDistance) {
            response = false;
            stringJoiner.add("주문할 수 없는 거리입니다.");
        }

        Integer minimumOrderPrice = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new)
            .getMinimumOrderPrice();

        if (totalPrice < minimumOrderPrice) {
            response = false;
            stringJoiner.add("총 금액이 최소 주문 금액 미만입니다.");
        }

        return new SelectOrderPossibleResponseDto(response, stringJoiner.toString());
    }
}
