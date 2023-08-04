package store.cookshoong.www.cookshoongbackend.payment.repository.charge;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.QOrder;
import store.cookshoong.www.cookshoongbackend.payment.entity.QCharge;

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
    public String lookupFindByPaymentKey(UUID orderCode) {
        QCharge charge = QCharge.charge;
        QOrder order = QOrder.order;

        return jpaQueryFactory
            .select(Projections.constructor(String.class, charge.paymentKey))
            .from(charge)
            .where(charge.order.orderCode.eq(orderCode))
            .fetchOne();
    }
}
