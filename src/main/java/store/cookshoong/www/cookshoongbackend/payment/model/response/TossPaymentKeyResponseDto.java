package store.cookshoong.www.cookshoongbackend.payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Toss 에서 제공한 paymentKey 를 Charge DB 에서 받아오는 DTO.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Getter
@AllArgsConstructor
public class TossPaymentKeyResponseDto {

    private String paymentKey;
}
