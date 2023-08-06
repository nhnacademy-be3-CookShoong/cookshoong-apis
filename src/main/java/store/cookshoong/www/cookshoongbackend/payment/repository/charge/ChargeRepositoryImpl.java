package store.cookshoong.www.cookshoongbackend.payment.repository.charge;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import store.cookshoong.www.cookshoongbackend.payment.entity.QCharge;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TossPaymentKeyResponseDto;

/**
 * 결제에 대한 Repository.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
public class ChargeRepositoryImpl implements ChargeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ChargeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TossPaymentKeyResponseDto lookupFindByPaymentKey(UUID orderCode) {
        QCharge charge = QCharge.charge;

        return jpaQueryFactory
            .select(Projections.constructor(TossPaymentKeyResponseDto.class, charge.paymentKey))
            .from(charge)
            .where(charge.order.code.eq(orderCode))
            .fetchOne();
    }

    @Override
    public Integer findChargedAmountByChargeCode(UUID chargeCode) {
        QCharge charge = QCharge.charge;

        return jpaQueryFactory
            .select(charge.chargedAmount)
            .from(charge)
            .where(charge.code.eq(chargeCode))
            .fetchOne();
    }
}
