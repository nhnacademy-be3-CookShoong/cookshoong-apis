package store.cookshoong.www.cookshoongbackend.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailResponseDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.repository.charge.ChargeRepository;
import store.cookshoong.www.cookshoongbackend.point.entity.PointLog;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReasonOrder;
import store.cookshoong.www.cookshoongbackend.point.repository.PointLogRepository;
import store.cookshoong.www.cookshoongbackend.point.repository.PointReasonOrderRepository;
import store.cookshoong.www.cookshoongbackend.point.repository.PointReasonRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@ExtendWith(MockitoExtension.class)
class PointEventServiceTest {
    @InjectMocks
    PointEventService pointEventService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    PointReasonRepository pointReasonRepository;
    @Mock
    PointLogRepository pointLogRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderDetailRepository orderDetailRepository;
    @Mock
    ChargeRepository chargeRepository;
    @Mock
    PointReasonOrderRepository pointReasonOrderRepository;
    @Spy
    TestEntity te;

    List<LookupOrderDetailResponseDto> details;

    int totalCost;

    @BeforeEach
    void beforeEach() {
        details = List.of(
            new LookupOrderDetailResponseDto(1_000, 1, new BigDecimal("1.0")),
            new LookupOrderDetailResponseDto(1_500, 2, new BigDecimal("2.0")),
            new LookupOrderDetailResponseDto(2_000, 3, null)
        );

        totalCost = 0;

        for (LookupOrderDetailResponseDto detail : details) {
            totalCost += detail.getNowCost() * detail.getCount();
        }
    }

    @Test
    @DisplayName("회원가입 포인트 제공 성공")
    void createSignupPointAccountSuccessTest() throws Exception {
        assertDoesNotThrow(() ->
            pointEventService.createSignupPoint(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("주문 포인트 제공 실패 - 주문 정보 없음")
    void createOrderPointOrderNotFoundFailTest() throws Exception {
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class, () ->
            pointEventService.createPaymentPoint(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 포인트 제공 성공")
    void createOrderPointSuccessTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        Account account = mock(Account.class);
        when(order.getAccount())
            .thenReturn(account);

        Store store = mock(Store.class);
        when(order.getStore())
            .thenReturn(store);

        BigDecimal defaultEarningRate = new BigDecimal("3.0");
        when(store.getDefaultEarningRate())
            .thenReturn(defaultEarningRate);

        when(orderDetailRepository.lookupOrderDetailForPoint(any(UUID.class)))
            .thenReturn(details);

        when(pointLogRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Charge charge = mock(Charge.class);
        when(chargeRepository.findByOrder(any(Order.class)))
            .thenReturn(Optional.of(charge));

        when(charge.getChargedAmount())
            .thenReturn(totalCost);

        assertDoesNotThrow(() ->
            pointEventService.createPaymentPoint(UUID.randomUUID()));

        ArgumentCaptor<PointLog> pointLogCaptor = ArgumentCaptor.forClass(PointLog.class);
        verify(pointLogRepository).save(pointLogCaptor.capture());

        int point = pointLogCaptor.getValue().getPointMovement();

        int testPoint = 0;
        for (LookupOrderDetailResponseDto detail : details) {
            testPoint += (int) (detail.getNowCost() * detail.getCount() / 100 * detail.getEarningRate());
        }

        assertThat(point).isEqualTo(testPoint);
    }

    @Test
    @DisplayName("주문 포인트 제공 성공 - 할인값")
    void createOrderPointDiscountAmountSuccessTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        Account account = mock(Account.class);
        when(order.getAccount())
            .thenReturn(account);

        Store store = mock(Store.class);
        when(order.getStore())
            .thenReturn(store);

        BigDecimal defaultEarningRate = new BigDecimal("3.0");
        when(store.getDefaultEarningRate())
            .thenReturn(defaultEarningRate);

        when(orderDetailRepository.lookupOrderDetailForPoint(any(UUID.class)))
            .thenReturn(details);

        when(pointLogRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Charge charge = mock(Charge.class);
        when(chargeRepository.findByOrder(any(Order.class)))
            .thenReturn(Optional.of(charge));

        double afterDiscountPercent = 0.8;

        when(charge.getChargedAmount())
            .thenReturn((int) (totalCost * afterDiscountPercent));

        assertDoesNotThrow(() ->
            pointEventService.createPaymentPoint(UUID.randomUUID()));

        ArgumentCaptor<PointLog> pointLogCaptor = ArgumentCaptor.forClass(PointLog.class);
        verify(pointLogRepository).save(pointLogCaptor.capture());

        int point = pointLogCaptor.getValue().getPointMovement();

        int testPoint = 0;
        for (LookupOrderDetailResponseDto detail : details) {
            testPoint += (int) (detail.getNowCost() * detail.getCount() / 100 * detail.getEarningRate());
        }

        int expected = (int) (testPoint * afterDiscountPercent);
        assertThat(point).isEqualTo(expected);
    }
}
