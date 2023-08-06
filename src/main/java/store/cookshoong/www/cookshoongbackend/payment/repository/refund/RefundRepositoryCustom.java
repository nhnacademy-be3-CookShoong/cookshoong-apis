package store.cookshoong.www.cookshoongbackend.payment.repository.refund;


import java.util.UUID;

/**
 * 환불에 대한 Repository.
 *
 * @author jeongjewan
 * @since 2023.08.04
 */
public interface RefundRepositoryCustom {

    Integer findRefundTotalAmount(UUID chargeCode);
}
