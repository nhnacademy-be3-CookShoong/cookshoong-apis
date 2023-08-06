package store.cookshoong.www.cookshoongbackend.payment.controller;

import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeValidationException;
import store.cookshoong.www.cookshoongbackend.payment.exception.RefundValidationException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateRefundDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TossPaymentKeyResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.service.PaymentService;

/**
 * 결제에 대한 Controller.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     *  결제 승인 후 결제에 성공한 결제정보가 담겨져 오는 Controller.
     *
     * @param createPaymentDto      성공한 결제 정보
     * @param bindingResult         Validation 에 대한 bindingResult
     * @return                      상태코드 201(CREATED)와 함께 응답을 반환
     */
    @PostMapping("/charges")
    public ResponseEntity<Void> postCreatePayment(@RequestBody @Valid CreatePaymentDto createPaymentDto,
                                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ChargeValidationException(bindingResult);
        }

        paymentService.createPayment(createPaymentDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 토스 API 결제 조회 및 취소에 필요한 paymentKey 를 응답해주는 Controller.
     *
     * @param orderCode     주문 코드
     * @return              상태코드 200(Ok)와 함께 응답을 반환 & paymentKey 를 반환
     */
    @GetMapping("/{orderCode}/select-paymentKey")
    public ResponseEntity<TossPaymentKeyResponseDto> getTossPaymentKey(@PathVariable UUID orderCode) {

        TossPaymentKeyResponseDto keyResponseDto = paymentService.selectTossPaymentKey(orderCode);

        return ResponseEntity.ok(keyResponseDto);
    }

    /**
     * 주문에 대해 결제 취소 성공 후 활불 요청을 처리하는 메서드.
     *
     * @param createRefundDto       환불에 대한 Dto
     * @param bindingResult         Validation 에 대한 bindingResult
     * @return                      상태코드 201(CREATED)와 함께 응답을 반환
     */
    @PostMapping("/refunds")
    public ResponseEntity<CreateRefundDto> postCreateRefund(@RequestBody @Valid CreateRefundDto createRefundDto,
                                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new RefundValidationException(bindingResult);
        }

        paymentService.createRefund(createRefundDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 해당 결제에 대해 환불금액이 결제금액보다 넘어가는지 검증하는 메서드.
     *
     * @param chargeCode        결제 코드
     * @param refundAmount      환불 금액
     * @return                  상태코드 200(Ok)와 함께 응답을 반환 & 환불금액이 결제 금액을 넘으면 true 반환 그렇지 않으면 false 반환
     */
    @GetMapping("/charges/{chargeCode}/refunds/verify")
    public ResponseEntity<Boolean> getIsRefundAmountExceedsChargedAmount(@PathVariable UUID chargeCode,
                                                                         @RequestParam Integer refundAmount) {

        Boolean isRefundAmount = paymentService.isRefundAmountExceedsChargedAmount(refundAmount, chargeCode);

        return ResponseEntity.ok(isRefundAmount);
    }
}
