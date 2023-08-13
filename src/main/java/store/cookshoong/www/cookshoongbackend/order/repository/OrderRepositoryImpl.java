package store.cookshoong.www.cookshoongbackend.order.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu.menu;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrder.order;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetail.orderDetail;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetailMenuOption.orderDetailMenuOption;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderStatus.orderStatus;
import static store.cookshoong.www.cookshoongbackend.payment.entity.QCharge.charge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderDetailMenuOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderDetailMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 주문 관련 복잡한 쿼리를 처리하는 impl.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LookupOrderInStatusResponseDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode) {
        List<LookupOrderInStatusResponseDto> orders = getOrderInStatus(store, orderStatusCode);

        Set<Long> orderDetailIds = orders.stream()
            .flatMap(selectOrderInProgressDto -> selectOrderInProgressDto.getSelectOrderDetails().stream()
                .map(LookupOrderDetailMenuResponseDto::getOrderDetailId))
            .collect(Collectors.toSet());

        Map<Long, List<LookupOrderDetailMenuOptionResponseDto>> selectOrderDetailMenuOptions =
            getOrderDetailMenuOptions(orderDetailIds);

        orders.stream()
            .flatMap(selectOrderInProgress -> selectOrderInProgress.getSelectOrderDetails().stream())
            .forEach(selectOrderDetail -> selectOrderDetail.setSelectOrderDetailMenuOptions(
                selectOrderDetailMenuOptions.getOrDefault(selectOrderDetail.getOrderDetailId(), Collections.emptyList())
            ));

        return orders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<LookupOrderInStatusResponseDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode,
                                                                    Pageable pageable) {
        List<LookupOrderInStatusResponseDto> lookupOrderInStatusResponses = lookupOrderInStatus(store, orderStatusCode);
        Long totalSize = getOrderInStatusSize(store, orderStatusCode);

        return new PageImpl<>(lookupOrderInStatusResponses, pageable, totalSize);
    }

    private List<LookupOrderInStatusResponseDto> getOrderInStatus(Store store, Set<String> orderStatusCode) {
        return jpaQueryFactory
            .selectFrom(order)
            .innerJoin(order.orderStatus, orderStatus)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(orderDetail.menu, menu)

            .innerJoin(charge)
            .on(charge.order.eq(order))

            .where(order.store.eq(store), orderStatus.code.in(orderStatusCode))
            .orderBy(order.orderedAt.asc())

            .transform(
                groupBy(order)
                    .list(new QLookupOrderInStatusResponseDto(
                        order.code,
                        orderStatus.description,
                        list(new QLookupOrderDetailMenuResponseDto(
                            orderDetail.id, orderDetail.nowName, menu.cookingTime, orderDetail.count)),
                        order.memo,
                        charge.code,
                        charge.chargedAmount,
                        charge.paymentKey,
                        order.orderedAt,
                        order.deliveryAddress))
            );
    }

    private Map<Long, List<LookupOrderDetailMenuOptionResponseDto>> getOrderDetailMenuOptions(Set<Long> orderDetailIds) {
        return jpaQueryFactory
            .selectFrom(orderDetailMenuOption)
            .innerJoin(orderDetailMenuOption.orderDetail, orderDetail)
            .where(orderDetail.id.in(orderDetailIds))
            .transform(
                groupBy(orderDetail.id)
                    .as(list(new QLookupOrderDetailMenuOptionResponseDto(orderDetailMenuOption.nowName)))
            );
    }

    private Long getOrderInStatusSize(Store store, Set<String> orderStatusCode) {
        return jpaQueryFactory
                .select(order.code.count())
                .from(order)
                .innerJoin(order.orderStatus, orderStatus)

                .innerJoin(orderDetail)
                .on(orderDetail.order.eq(order))

                .innerJoin(orderDetail.menu, menu)

                .innerJoin(charge)
                .on(charge.order.eq(order))

                .where(order.store.eq(store), orderStatus.code.in(orderStatusCode))

                .fetchOne();
    }
}
