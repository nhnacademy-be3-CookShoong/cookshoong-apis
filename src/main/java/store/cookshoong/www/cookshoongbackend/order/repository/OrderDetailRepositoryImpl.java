package store.cookshoong.www.cookshoongbackend.order.repository;

import static store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu.menu;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrder.order;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetail.orderDetail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderDetailResponseDto;

/**
 * QueryDSL OrderDetailRepository.
 * 주문 정보에서 필요한 정보들을 반환하도록 한다.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LookupOrderDetailResponseDto> lookupOrderDetailForPoint(UUID orderCode) {
        return queryFactory
            .select(new QLookupOrderDetailResponseDto(
                orderDetail.nowCost, orderDetail.count, menu.earningRate))

            .from(orderDetail)

            .innerJoin(orderDetail.order, order)
            .on(order.code.eq(orderCode))

            .innerJoin(orderDetail.menu, menu)

            .fetch();
    }
}
