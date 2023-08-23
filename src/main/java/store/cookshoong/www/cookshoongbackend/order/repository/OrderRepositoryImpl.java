package store.cookshoong.www.cookshoongbackend.order.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static store.cookshoong.www.cookshoongbackend.coupon.entity.QCouponLog.couponLog;
import static store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.QMenu.menu;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrder.order;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetail.orderDetail;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetailMenuOption.orderDetailMenuOption;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderStatus.orderStatus;
import static store.cookshoong.www.cookshoongbackend.payment.entity.QCharge.charge;
import static store.cookshoong.www.cookshoongbackend.point.entity.QPointLog.pointLog;
import static store.cookshoong.www.cookshoongbackend.point.entity.QPointReasonOrder.pointReasonOrder;
import static store.cookshoong.www.cookshoongbackend.review.entity.QReview.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupAccountOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupAccountOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderDetailMenuOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderDetailMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.QLookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
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
    private final QStore store = new QStore("StoreEntity");

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LookupOrderInStatusResponseDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode) {
        List<LookupOrderInStatusResponseDto> orderInStatus = getOrderInStatus(store, orderStatusCode);
        updateOrderMenuOption(orderInStatus);
        return orderInStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<LookupOrderInStatusResponseDto> lookupOrderInStatus(Store store, Set<String> orderStatusCode,
                                                                    Pageable pageable) {
        List<LookupOrderInStatusResponseDto> orderInStatus =
            getOrderInStatus(store, orderStatusCode, pageable);
        updateOrderMenu(orderInStatus);
        updateOrderMenuOption(orderInStatus);
        Long totalSize = getOrderInStatusSize(store, orderStatusCode);
        return new PageImpl<>(orderInStatus, pageable, totalSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<LookupAccountOrderInStatusResponseDto> lookupOrderInStatus(Account account,
                                                                           Set<String> orderStatusCode,
                                                                           Pageable pageable) {
        List<LookupAccountOrderInStatusResponseDto> lookupAccountOrderInStatusResponses =
            getOrderInStatus(account, orderStatusCode, pageable);

        List<LookupOrderInStatusResponseDto> orderInStatus = lookupAccountOrderInStatusResponses.stream()
            .map(LookupAccountOrderInStatusResponseDto::getLookupOrderInStatusResponseDto)
            .collect(Collectors.toList());

        updateOrderMenu(orderInStatus);
        updateOrderMenuOption(orderInStatus);

        lookupAccountOrderInStatusResponses.forEach(LookupAccountOrderInStatusResponseDto::updateTotalOrderPrice);

        Long totalSize = getOrderInStatusSize(account, orderStatusCode);
        return new PageImpl<>(lookupAccountOrderInStatusResponses, pageable, totalSize);
    }

    private void updateOrderMenu(List<LookupOrderInStatusResponseDto> orders) {
        Set<UUID> orderCodes = orders.stream()
            .map(LookupOrderInStatusResponseDto::getOrderCode)
            .collect(Collectors.toSet());

        Map<UUID, List<LookupOrderDetailMenuResponseDto>> orderDetailMenus = getOrderDetailMenus(orderCodes);

        for (LookupOrderInStatusResponseDto order : orders) {

            List<LookupOrderDetailMenuResponseDto> orderMenus =
                orderDetailMenus.getOrDefault(order.getOrderCode(), Collections.emptyList());

            order.updateSelectOrderDetails(orderMenus);
        }
    }

    private void updateOrderMenuOption(List<LookupOrderInStatusResponseDto> orders) {
        Set<Long> orderDetailIds = orders.stream()
            .flatMap(selectOrderInProgressDto -> selectOrderInProgressDto.getSelectOrderDetails().stream()
                .map(LookupOrderDetailMenuResponseDto::getOrderDetailId))
            .collect(Collectors.toSet());

        Map<Long, List<LookupOrderDetailMenuOptionResponseDto>> selectOrderDetailMenuOptions =
            getOrderDetailMenuOptions(orderDetailIds);

        orders.stream()
            .flatMap(selectOrderInProgress -> selectOrderInProgress.getSelectOrderDetails().stream())
            .forEach(selectOrderDetail -> selectOrderDetail.updateSelectOrderDetailMenuOptions(
                selectOrderDetailMenuOptions.getOrDefault(selectOrderDetail.getOrderDetailId(), Collections.emptyList())
            ));
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
                            orderDetail.id, orderDetail.nowName, menu.cookingTime, orderDetail.count,
                            orderDetail.nowCost)),
                        order.memo,
                        charge.code,
                        charge.chargedAmount,
                        charge.paymentKey,
                        order.orderedAt,
                        order.deliveryAddress))
            );
    }

    private List<LookupOrderInStatusResponseDto> getOrderInStatus(Store store, Set<String> orderStatusCode,
                                                                  Pageable pageable) {
        return jpaQueryFactory
            .select(new QLookupOrderInStatusResponseDto(order.code, orderStatus.description, order.memo, charge.code,
                charge.chargedAmount, charge.paymentKey, order.orderedAt, order.deliveryAddress))
            .from(order)

            .innerJoin(order.orderStatus, orderStatus)

            .innerJoin(charge)
            .on(charge.order.eq(order))

            .where(order.store.eq(store), orderStatus.code.in(orderStatusCode))
            .orderBy(order.orderedAt.asc())

            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())

            .fetch();
    }

    private List<LookupAccountOrderInStatusResponseDto> getOrderInStatus(Account account, Set<String> orderStatusCode,
                                                                         Pageable pageable) {
        return jpaQueryFactory
            .select(new QLookupAccountOrderInStatusResponseDto(
                new QLookupOrderInStatusResponseDto(order.code, orderStatus.description, order.memo, charge.code,
                    charge.chargedAmount, charge.paymentKey, order.orderedAt, order.deliveryAddress),
                store.name,
                couponLog.discountAmount,
                pointLog.pointMovement,
                order.deliveryCost,
                review.id
            ))
            .from(order)
            .innerJoin(order.orderStatus, orderStatus)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(orderDetail.menu, menu)

            .innerJoin(charge)
            .on(charge.order.eq(order))

            .innerJoin(order.store, store)

            .leftJoin(couponLog)
            .on(couponLog.order.eq(order))

            .leftJoin(pointReasonOrder)
            .on(pointReasonOrder.order.eq(order))

            .leftJoin(pointLog)
            .on(pointLog.pointReason.id.eq(pointReasonOrder.id), pointLog.pointMovement.gt(0))

            .leftJoin(review)
            .on(review.order.eq(order))

            .where(order.account.eq(account), orderStatus.code.in(orderStatusCode))
            .orderBy(order.orderedAt.desc())

            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())

            .fetch();
    }

    private Map<UUID, List<LookupOrderDetailMenuResponseDto>> getOrderDetailMenus(
        Set<UUID> orderCodes) {
        return jpaQueryFactory
            .selectFrom(orderDetail)

            .innerJoin(orderDetail.order, order)

            .innerJoin(orderDetail.menu, menu)

            .where(order.code.in(orderCodes))
            .transform(
                groupBy(order.code)
                    .as(list(
                        new QLookupOrderDetailMenuResponseDto(
                            orderDetail.id, orderDetail.nowName, menu.cookingTime, orderDetail.count,
                            orderDetail.nowCost)))
            );
    }

    private Map<Long, List<LookupOrderDetailMenuOptionResponseDto>> getOrderDetailMenuOptions(
        Set<Long> orderDetailIds) {
        return jpaQueryFactory
            .selectFrom(orderDetailMenuOption)
            .innerJoin(orderDetailMenuOption.orderDetail, orderDetail)
            .where(orderDetail.id.in(orderDetailIds))
            .transform(
                groupBy(orderDetail.id)
                    .as(list(
                        new QLookupOrderDetailMenuOptionResponseDto(
                            orderDetailMenuOption.nowName,
                            orderDetailMenuOption.nowPrice)))
            );
    }

    private Long getOrderInStatusSize(Store store, Set<String> orderStatusCode) {
        return jpaQueryFactory
            .select(order.code.countDistinct())
            .from(order)
            .innerJoin(order.orderStatus, orderStatus)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(orderDetail.menu, menu)

            .innerJoin(charge)
            .on(charge.order.eq(order))

            .where(order.store.eq(store), orderStatus.code.in(orderStatusCode))

            .fetchFirst();
    }

    private Long getOrderInStatusSize(Account account, Set<String> orderStatusCode) {
        return jpaQueryFactory
            .select(order.code.countDistinct())

            .from(order)
            .innerJoin(order.orderStatus, orderStatus)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(orderDetail.menu, menu)

            .innerJoin(charge)
            .on(charge.order.eq(order))

            .innerJoin(order.store, store)

            .where(order.account.eq(account), orderStatus.code.in(orderStatusCode))

            .fetchFirst();
    }
}
