package store.cookshoong.www.cookshoongbackend.point.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailResponseDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.exception.ChargeNotFoundException;
import store.cookshoong.www.cookshoongbackend.payment.repository.charge.ChargeRepository;
import store.cookshoong.www.cookshoongbackend.point.entity.PointLog;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReasonOrder;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReasonSignup;
import store.cookshoong.www.cookshoongbackend.point.exception.OrderPointLogDuplicateException;
import store.cookshoong.www.cookshoongbackend.point.repository.PointLogRepository;
import store.cookshoong.www.cookshoongbackend.point.repository.PointReasonOrderRepository;
import store.cookshoong.www.cookshoongbackend.point.repository.PointReasonRepository;

/**
 * 포인트 이벤트 로직을 담당하는 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointEventService {
    private static final int SIGNUP_POINT = 4_000;
    private static final String ORDER_COMPLETE_POINT_EXPLAIN = "주문 완료로 인한 포인트 적립";
    private static final String ORDER_ABORT_POINT_EXPLAIN = "주문 취소로 인한 사용 포인트 환불";
    private static final String ORDER_POINT_LOG_EMPTY = "주문코드: `{}`에 대한 포인트 적립이 검색되지 않음.";

    private final AccountRepository accountRepository;
    private final PointReasonRepository pointReasonRepository;
    private final PointLogRepository pointLogRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ChargeRepository chargeRepository;

    /**
     * 회원가입 시 포인트 제공.
     *
     * @param accountId the account id
     */
    public void createSignupPoint(Long accountId) {
        Account account = accountRepository.getReferenceById(accountId);
        PointReasonSignup pointReasonSignup = pointReasonRepository.save(new PointReasonSignup(account));
        pointLogRepository.save(new PointLog(account, pointReasonSignup, SIGNUP_POINT));
    }

    /**
     * 주문 완료 시 주문에 대한 포인트 제공.
     *
     * @param orderCode the order code
     */
    public void createPaymentPoint(UUID orderCode) {
        Order order = orderRepository.findById(orderCode)
            .orElseThrow(OrderNotFoundException::new);

        Account account = order.getAccount();

        double defaultEarningRate = order.getStore()
            .getDefaultEarningRate()
            .doubleValue();

        List<LookupOrderDetailResponseDto> orderDetails = orderDetailRepository.lookupOrderDetailForPoint(orderCode);

        int originChargeAmount = orderDetails.stream()
            .mapToInt(LookupOrderDetailResponseDto::getTotalCost)
            .reduce(Integer::sum)
            .orElseThrow(NoSuchElementException::new);

        int originPoint = orderDetails.stream()
            .mapToInt(orderDetail -> getOriginPoint(orderDetail, defaultEarningRate))
            .reduce(Integer::sum)
            .orElseThrow(NoSuchElementException::new);

        Charge charge = chargeRepository.findByOrder(order)
            .orElseThrow(ChargeNotFoundException::new);

        int point = (int) ((double) charge.getChargedAmount() / originChargeAmount * originPoint);

        PointReasonOrder pointReasonOrder = pointReasonRepository.save(
            new PointReasonOrder(order, ORDER_COMPLETE_POINT_EXPLAIN));
        pointLogRepository.save(new PointLog(account, pointReasonOrder, point));
    }

    private int getOriginPoint(LookupOrderDetailResponseDto orderDetail, double defaultEarningRate) {
        if (Objects.isNull(orderDetail.getEarningRate())) {
            orderDetail.setEarningRate(defaultEarningRate);
        }

        double earningRate = orderDetail.getEarningRate();

        return (int) (orderDetail.getTotalCost() * earningRate / 100);
    }

    /**
     * 주문 중단에 의한 포인트 환불.
     *
     * @param orderCode the order code
     */
    public void refundOrderPoint(UUID orderCode) {
        Order order = orderRepository.findById(orderCode)
            .orElseThrow(OrderNotFoundException::new);

        List<PointLog> pointLogs = pointLogRepository.lookupPointCompleteAmount(order)
            .stream()
            .filter(pointLog -> pointLog.getPointMovement() > 0)
            .collect(Collectors.toList());

        if (pointLogs.isEmpty()) {
            log.warn(ORDER_POINT_LOG_EMPTY, orderCode);
            return;
        }

        if (pointLogs.size() > 1) {
            throw new OrderPointLogDuplicateException();
        }

        PointLog pointLog = pointLogs.get(0);
        Account account = pointLog.getAccount();
        int pointMovement = pointLog.getPointMovement();

        pointLogRepository.save(
            new PointLog(account, new PointReasonOrder(order, ORDER_ABORT_POINT_EXPLAIN), pointMovement));
    }
}
