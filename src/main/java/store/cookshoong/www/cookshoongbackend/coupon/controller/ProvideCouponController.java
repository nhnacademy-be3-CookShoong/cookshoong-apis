package store.cookshoong.www.cookshoongbackend.coupon.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponRequestValidationException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.ProvideCouponService;
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;

/**
 * 쿠폰 발급 RestController.
 *
 * @author eora21 (김주호)
 * @since 2023.07.28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/provide")
public class ProvideCouponController {
    private final ProvideCouponService provideCouponService;
    private final RabbitTemplate rabbitTemplate;
    private final LockProcessor lockProcessor;

    /**
     * 사용자에게 쿠폰을 발급하는 메서드.
     *
     * @param updateProvideCouponRequestDto the offer coupon request
     * @param bindingResult                 the binding result
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Void> postProvideCouponToAccount(@RequestBody @Valid
                                                           UpdateProvideCouponRequestDto updateProvideCouponRequestDto,
                                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IssueCouponRequestValidationException(bindingResult);
        }

        lockProcessor.lock(updateProvideCouponRequestDto.getCouponPolicyId().toString(), ignore ->
            provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 이벤트를 통해 쿠폰을 발급하는 메서드. rabbitMq로 메시지를 전달한다.
     *
     * @param requestDto the update provide coupon request
     * @param bindingResult                 the binding result
     * @return the response entity
     */
    @PostMapping("/event")
    public ResponseEntity<Void> postProvideCouponToAccountByEvent(@RequestBody @Valid
                                                                  UpdateProvideCouponRequestDto requestDto,
                                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IssueCouponRequestValidationException(bindingResult);
        }

        provideCouponService.validBeforeProvide(requestDto.getAccountId(), requestDto.getCouponPolicyId());

        rabbitTemplate.convertAndSend(requestDto);

        return ResponseEntity
            .ok()
            .build();
    }
}
