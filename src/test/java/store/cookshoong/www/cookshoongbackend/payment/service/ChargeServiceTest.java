package store.cookshoong.www.cookshoongbackend.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.order.OrderRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.charge.ChargeRepository;
import store.cookshoong.www.cookshoongbackend.payment.repository.chargetype.ChargeTypeRepository;

/**
 * Charge Service 테스트.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@ExtendWith(MockitoExtension.class)
class ChargeServiceTest {

    @InjectMocks
    private ChargeService chargeService;

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private ChargeTypeRepository chargeTypeRepository;

    @Mock
    private OrderRepository orderRepository;

    UUID uuid = UUID.randomUUID();

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장")
    void createPayment() {

        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", uuid.toString());
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");

        Order mockOrder = ReflectionUtils.newInstance(Order.class);
        ChargeType mockChargeType = ReflectionUtils.newInstance(ChargeType.class);

        when(orderRepository.findById(uuid)).thenReturn(Optional.of(mockOrder));
        when(chargeTypeRepository.findById("toss")).thenReturn(Optional.of(mockChargeType));

        chargeService.createPayment(createPaymentDto);

        verify(chargeRepository, times(1)).save(any(Charge.class));
        verify(orderRepository, times(1)).findById(uuid);
        verify(chargeTypeRepository, times(1)).findById("toss");
    }
}
