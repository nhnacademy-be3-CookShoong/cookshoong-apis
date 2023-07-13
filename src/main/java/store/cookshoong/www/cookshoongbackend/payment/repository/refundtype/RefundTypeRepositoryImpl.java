package store.cookshoong.www.cookshoongbackend.payment.repository.refundtype;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import store.cookshoong.www.cookshoongbackend.payment.entity.QRefundType;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;

/**
 * 환불 타입에 해당되는 QueryDsl class.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
public class RefundTypeRepositoryImpl implements RefundTypeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public RefundTypeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TypeResponseDto> lookupRefundTypeAll() {
        QRefundType refundType = QRefundType.refundType;

        return jpaQueryFactory
            .select(Projections.constructor(TypeResponseDto.class, refundType.code, refundType.name))
            .from(refundType)
            .where(refundType.isDeleted.eq(false))
            .fetch();
    }
}
