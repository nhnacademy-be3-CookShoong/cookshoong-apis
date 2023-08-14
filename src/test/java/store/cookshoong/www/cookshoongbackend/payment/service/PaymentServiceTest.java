package store.cookshoong.www.cookshoongbackend.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderStatusRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.entity.Refund;
import store.cookshoong.www.cookshoongbackend.payment.entity.RefundType;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateRefundDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TossPaymentKeyResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.charge.ChargeRepository;
import store.cookshoong.www.cookshoongbackend.payment.repository.chargetype.ChargeTypeRepository;
import store.cookshoong.www.cookshoongbackend.payment.repository.refund.RefundRepository;
import store.cookshoong.www.cookshoongbackend.payment.repository.refundtype.RefundTypeRepository;

/**
 * Charge Service 테스트.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private ChargeTypeRepository chargeTypeRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private RefundTypeRepository refundTypeRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    UUID uuid = UUID.randomUUID();

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장")
    void createPayment() {

        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", uuid);
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");

        Order mockOrder = ReflectionUtils.newInstance(Order.class);
        ChargeType mockChargeType = ReflectionUtils.newInstance(ChargeType.class);

        when(orderRepository.findById(uuid)).thenReturn(Optional.of(mockOrder));
        when(chargeTypeRepository.findById("toss")).thenReturn(Optional.of(mockChargeType));

        OrderStatus orderStatus = mock(OrderStatus.class);
        when(orderStatusRepository.findByOrderStatusCode(OrderStatus.StatusCode.PAY))
            .thenReturn(Optional.of(orderStatus));

        paymentService.createPayment(createPaymentDto);

        verify(chargeRepository, times(1)).save(any(Charge.class));
        verify(orderRepository, times(1)).findById(uuid);
        verify(chargeTypeRepository, times(1)).findById("toss");
    }

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장 실패 - 주문을 찾을 수 없는 경우")
    void createPayment_OrderNotFound() {

        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", uuid);
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");

        when(orderRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> paymentService.createPayment(createPaymentDto));
    }

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장 실패 - 결제코드를 찾을 수 없는 경우")
    void createPayment_ChargeTypeNotFound() {

        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", uuid);
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");

        Order mockOrder = ReflectionUtils.newInstance(Order.class);
        when(orderRepository.findById(uuid)).thenReturn(Optional.of(mockOrder));
        when(chargeTypeRepository.findById(createPaymentDto.getPaymentType())).thenReturn(Optional.empty());

        OrderStatus orderStatus = mock(OrderStatus.class);
        when(orderStatusRepository.findByOrderStatusCode(OrderStatus.StatusCode.PAY))
            .thenReturn(Optional.of(orderStatus));

        assertThrows(ChargeTypeNotFoundException.class, () -> paymentService.createPayment(createPaymentDto));
    }

    @Test
    @DisplayName("주문에 대해 환불한 데이터를 DB 에 저장하는 메서드")
    void createRefund() {

        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", uuid);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        Charge charge = ReflectionUtils.newInstance(Charge.class);
        RefundType refundType = ReflectionUtils.newInstance(RefundType.class);

        when(chargeRepository.findById(uuid)).thenReturn(Optional.of(charge));
        when(refundTypeRepository.findById("partial")).thenReturn(Optional.of(refundType));

        paymentService.createRefund(createRefundDto);

        verify(refundRepository, times(1)).save(any(Refund.class));
        verify(chargeRepository, times(1)).findById(uuid);
        verify(refundTypeRepository, times(1)).findById("partial");
    }

    @Test
    @DisplayName("주문에 대해 환불한 데이터를 DB 에 저장 실패 - 결제가 존재하지 않는 경우")
    void createRefund_ChargeNotFound() {

        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", uuid);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        when(chargeRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(ChargeNotFoundException.class, () -> paymentService.createRefund(createRefundDto));
    }

    @Test
    @DisplayName("주문에 대해 환불한 데이터를 DB 에 저장 실패 - 결제가 존재하지 않는 경우")
    void createRefund_RefundTypeNotFound() {

        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", uuid);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        Charge charge = ReflectionUtils.newInstance(Charge.class);
        when(chargeRepository.findById(uuid)).thenReturn(Optional.of(charge));
        when(refundTypeRepository.findById(createRefundDto.getRefundType())).thenReturn(Optional.empty());

        assertThrows(ChargeTypeNotFoundException.class, () -> paymentService.createRefund(createRefundDto));
    }

    @Test
    @DisplayName("orderCode 를 통해서 결제에 저장되어 있는 paymentKey 를 가져오는 메서드")
    void selectTossPaymentKey() {

        TossPaymentKeyResponseDto paymentKeyResponseDto = new TossPaymentKeyResponseDto("paymentKey");

        when(chargeRepository.lookupFindByPaymentKey(uuid)).thenReturn(paymentKeyResponseDto);

        TossPaymentKeyResponseDto actual = paymentService.selectTossPaymentKey(uuid);

        assertEquals(paymentKeyResponseDto.getPaymentKey(), actual.getPaymentKey());
    }

    @Test
    @DisplayName("환불 금액이 결제 금액을 초과하는지 검증하는 메서드 테스트")
    void isRefundAmountExceedsChargedAmount() {

        Integer refundAmount = 5000;

        Integer chargedAmount = 10000;
        Integer refundedTotalAmount = 6000;

        when(chargeRepository.findChargedAmountByChargeCode(uuid)).thenReturn(chargedAmount);
        when(refundRepository.findRefundTotalAmount(uuid)).thenReturn(refundedTotalAmount);

        boolean actual = paymentService.isRefundAmountExceedsChargedAmount(refundAmount, uuid);

        assertTrue(actual);
    }
}
