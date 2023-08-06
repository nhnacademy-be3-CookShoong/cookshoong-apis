package store.cookshoong.www.cookshoongbackend.payment.repository.charge;

import java.util.UUID;
import org.springframework.data.repository.NoRepositoryBean;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TossPaymentKeyResponseDto;

/**
 * 결제에 대한 Repository Custom.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@NoRepositoryBean
public interface ChargeRepositoryCustom {

    /**
     * 주문 아이디를 통해 paymentKey 를 가져오는 메서드.
     *
     * @return              PaymentKey 반환
     */
    TossPaymentKeyResponseDto lookupFindByPaymentKey(UUID orderCode);

    Integer findChargedAmountByChargeCode(UUID chargeCode);
}
