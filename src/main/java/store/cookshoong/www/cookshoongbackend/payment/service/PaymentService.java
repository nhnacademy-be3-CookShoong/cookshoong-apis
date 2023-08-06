package store.cookshoong.www.cookshoongbackend.payment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
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
 * 결제에 대한 Service.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final ChargeRepository chargeRepository;
    private final ChargeTypeRepository chargeTypeRepository;
    private final RefundRepository refundRepository;
    private final RefundTypeRepository refundTypeRepository;
    private final OrderRepository orderRepository;

    /**
     * 결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장하는 메서드.
     *
     * @param createPaymentDto      결제 성공한 정보 Dto
     */
    public void createPayment(CreatePaymentDto createPaymentDto) {

        Order order =
            orderRepository.findById(createPaymentDto.getOrderId()).orElseThrow(OrderNotFoundException::new);
        ChargeType chargeType =
            chargeTypeRepository.findById(
                createPaymentDto.getPaymentType()).orElseThrow(ChargeTypeNotFoundException::new);

        Charge charge =
            new Charge(chargeType, order, getApproveChargeAt(createPaymentDto.getChargedAt()),
                createPaymentDto.getChargedAmount(), createPaymentDto.getPaymentKey());

        chargeRepository.save(charge);
    }

    /**
     * 주문에 대해서 취소할 때 방생하는 환불 데이터를 저장하는 메서드.
     *
     * @param createRefundDto       환불에 대한 Dto
     */
    public void createRefund(CreateRefundDto createRefundDto) {

        Charge charge =
            chargeRepository.findById(createRefundDto.getChargeCode()).orElseThrow(ChargeNotFoundException::new);
        RefundType refundType =
            refundTypeRepository.findById(
                createRefundDto.getRefundType()).orElseThrow(ChargeTypeNotFoundException::new);


        Refund refund =
            new Refund(refundType, charge,
                getApproveChargeAt(createRefundDto.getRefundAt()), createRefundDto.getRefundAmount());

        refundRepository.save(refund);
    }

    /**
     * orderCode 를 통해서 결제에 저장되어 있는 paymentKey 를 가져오는 메서드.
     *
     * @param orderCode     주문 코드
     * @return              paymentKey 반환
     */
    @Transactional(readOnly = true)
    public TossPaymentKeyResponseDto selectTossPaymentKey(UUID orderCode) {

        return chargeRepository.lookupFindByPaymentKey(orderCode);
    }

    /**
     * 환불 금액이 결제 금액 보다 넘어가는지 검증하는 메서드.
     *
     * @param refundAmount      현재 환불되는 금액
     * @param chargeCode        이전 결제된 금액
     * @return                  환불 금액이 결제 금액을 넘으면 true, 환불 금액이 결제 금애보다 작으면 false
     */
    public boolean isRefundAmountExceedsChargedAmount(Integer refundAmount, UUID chargeCode) {

        Integer chargedAmount = chargeRepository.findChargedAmountByChargeCode(chargeCode);
        Integer refundedTotalAmount = refundRepository.findRefundTotalAmount(chargeCode);

        return refundAmount + refundedTotalAmount > chargedAmount;
    }

    /**
     *  String 형태를 LocalDateTime 으로 변환기.
     */
    private LocalDateTime getApproveChargeAt(String chargeAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        return LocalDateTime.parse(chargeAt, formatter);
    }
}
