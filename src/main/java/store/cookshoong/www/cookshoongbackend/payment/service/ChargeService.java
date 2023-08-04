package store.cookshoong.www.cookshoongbackend.payment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.order.OrderRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
import store.cookshoong.www.cookshoongbackend.payment.repository.charge.ChargeRepository;
import store.cookshoong.www.cookshoongbackend.payment.repository.chargetype.ChargeTypeRepository;

/**
 * 결제에 대한 Service.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ChargeService {

    private final ChargeRepository chargeRepository;
    private final ChargeTypeRepository chargeTypeRepository;
    private final OrderRepository orderRepository;

    /**
     * 결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장하는 메서드.
     *
     * @param createPaymentDto      결제 성공한 정보 Dto
     */
    public void createPayment(CreatePaymentDto createPaymentDto) {

        // orderRepository 에서 CreatePaymentDto 결제 성공한 orderId 를 가지고 조회한 후 Payment 저장.
        Order order = orderRepository.findById(UUID.fromString(createPaymentDto.getOrderId())).orElse(null);
        ChargeType chargeType = chargeTypeRepository.findById(createPaymentDto.getPaymentType()).orElse(null);

        Charge charge =
            new Charge(chargeType, order, getApproveChargeAt(createPaymentDto.getChargedAt()),
                createPaymentDto.getChargedAmount(), createPaymentDto.getPaymentKey());

        chargeRepository.save(charge);
    }

    /**
     *  String 형태를 LocalDateTime 으로 변환기.
     */
    private LocalDateTime getApproveChargeAt(String chargeAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        return LocalDateTime.parse(chargeAt, formatter);
    }
}
