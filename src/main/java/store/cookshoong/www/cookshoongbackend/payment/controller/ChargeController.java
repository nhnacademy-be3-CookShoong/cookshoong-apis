package store.cookshoong.www.cookshoongbackend.payment.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeValidationException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
import store.cookshoong.www.cookshoongbackend.payment.service.ChargeService;

/**
 * 결제에 대한 Controller.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@RestController
@RequestMapping("/api/payments/charges")
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;

    /**
     *  결제 승인 후 결제에 성공한 결제정보가 담겨져 오는 Controller.
     *
     * @param createPaymentDto      성공한 결제 정보
     * @param bindingResult         Validation 에 대한 bindingResult
     * @return                      상태코드 201(CREATED)와 함께 응답을 반환
     */
    @PostMapping
    public ResponseEntity<Void> postCreatePayment(@RequestBody @Valid CreatePaymentDto createPaymentDto,
                                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ChargeValidationException(bindingResult);
        }

        chargeService.createPayment(createPaymentDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
