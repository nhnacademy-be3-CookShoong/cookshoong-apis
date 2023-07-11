package store.cookshoong.www.cookshoongbackend.payment.repository.chargetype;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import store.cookshoong.www.cookshoongbackend.payment.entity.QChargeType;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;

/**
 * 결제 타입에 해당되는 QueryDsl class.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
public class ChargeTypeRepositoryImpl implements ChargeTypeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ChargeTypeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TypeResponseDto> lookupChargeTypeAll() {
        QChargeType chargeType = QChargeType.chargeType;

        return jpaQueryFactory
            .select(Projections.constructor(TypeResponseDto.class, chargeType.code, chargeType.name))
            .from(chargeType)
            .where(chargeType.isDeleted.eq(false))
            .fetch();
    }
}
