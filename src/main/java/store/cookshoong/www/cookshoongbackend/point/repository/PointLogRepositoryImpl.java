package store.cookshoong.www.cookshoongbackend.point.repository;

import static store.cookshoong.www.cookshoongbackend.point.entity.QPointLog.pointLog;
import static store.cookshoong.www.cookshoongbackend.point.entity.QPointReason.pointReason;
import static store.cookshoong.www.cookshoongbackend.point.entity.QPointReasonOrder.pointReasonOrder;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.point.entity.PointLog;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointLogResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.QPointLogResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.QPointResponseDto;

/**
 * 포인트 로그 QueryDSL.
 *
 * @author eora21 (김주호)
 * @since 2023.08.08
 */
@RequiredArgsConstructor
public class PointLogRepositoryImpl implements PointLogRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public PointResponseDto lookupMyPoint(Account account) {
        return queryFactory
            .select(new QPointResponseDto(pointLog.pointMovement.sum()))
            .from(pointLog)
            .where(pointLog.account.eq(account))
            .fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PointLogResponseDto> lookupMyPointLog(Account account, Pageable pageable) {
        List<PointLogResponseDto> couponLog = getCouponLog(account, pageable);
        Long couponLogTotalSize = getCouponLogTotalSize(account);
        return new PageImpl<>(couponLog, pageable, couponLogTotalSize);
    }

    private List<PointLogResponseDto> getCouponLog(Account account, Pageable pageable) {
        return queryFactory
            .select(new QPointLogResponseDto(pointReason, pointLog.pointMovement, pointLog.pointAt))
            .from(pointLog)

            .innerJoin(pointLog.pointReason, pointReason)

            .where(pointLog.account.eq(account))
            .orderBy(pointLog.id.desc())

            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())

            .fetch();
    }

    private Long getCouponLogTotalSize(Account account) {
        return queryFactory
            .select(pointLog.count())
            .from(pointLog)

            .innerJoin(pointLog.pointReason, pointReason)

            .where(pointLog.account.eq(account))

            .fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointLog lookupUsePoint(Order order) {
        return queryFactory
            .select(pointLog)
            .from(pointReasonOrder)

            .innerJoin(pointReasonOrder)
            .on(pointReasonOrder.order.eq(order))

            .innerJoin(pointLog)
            .on(pointLog.pointReason.id.eq(pointReasonOrder.id))

            .where(pointLog.pointMovement.lt(0))

            .fetchOne();
    }
}
