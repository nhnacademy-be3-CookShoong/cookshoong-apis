package store.cookshoong.www.cookshoongbackend.payment.repository.refund;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import store.cookshoong.www.cookshoongbackend.payment.entity.QRefund;

/**
 * 환불에 대한 Repository.
 *
 * @author jeongjewan
 * @since 2023.08.04
 */
public class RefundRepositoryImpl implements RefundRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public RefundRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Integer findRefundTotalAmount(UUID chargeCode) {
        QRefund refund = QRefund.refund;

        Integer totalRefundAmount = jpaQueryFactory
            .select(refund.refundAmount.sum())
            .from(refund)
            .where(refund.charge.code.eq(chargeCode))
            .fetchOne();

        return totalRefundAmount != null ? totalRefundAmount : 0;
    }
}
