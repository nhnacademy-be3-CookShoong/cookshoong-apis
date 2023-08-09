package store.cookshoong.www.cookshoongbackend.order.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import store.cookshoong.www.cookshoongbackend.order.exception.OrderStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.exception.PriceIncreaseException;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.model.request.PatchOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailMenuOptionRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderStatusRepository;
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

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMenuOptionRepository orderDetailMenuOptionRepository;
    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;

    /**
     * 주문 생성.
     *
     * @param createOrderRequestDto the create order request dto
     * @param cartItems             the cart items
     */
    public void createOrder(CreateOrderRequestDto createOrderRequestDto, List<CartRedisDto> cartItems) {
        Account account = accountRepository.findById(createOrderRequestDto.getAccountId())
            .orElseThrow(UserNotFoundException::new);

        OrderStatus orderStatusCreate = orderStatusRepository.findById(OrderStatus.StatusCode.CREATE.toString())
            .orElseThrow(OrderStatusNotFoundException::new);

        Store store = storeRepository.findById(createOrderRequestDto.getStoreId())
            .orElseThrow(StoreNotFoundException::new);

        if (!store.getStoreStatus().getCode().equals(STATUS_OPEN)) {
            throw new StoreNotOpenException();
        }

        Order order = orderRepository.save(new Order(createOrderRequestDto.getOrderCode(), orderStatusCreate, account,
            store, createOrderRequestDto.getMemo()));

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
    public void changeStatus(@Valid UUID orderCode, OrderStatus.StatusCode statusCode) {
        Order order = orderRepository.findById(orderCode)
            .orElseThrow(StoreNotFoundException::new);

        OrderStatus orderStatus = orderStatusRepository.findById(statusCode.toString())
            .orElseThrow(OrderStatusNotFoundException::new);

        order.updateOrderStatus(orderStatus);
    }
}
