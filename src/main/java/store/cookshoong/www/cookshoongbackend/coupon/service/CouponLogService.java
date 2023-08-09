package store.cookshoong.www.cookshoongbackend.coupon.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLog;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLogType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponLogNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponLogTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponLogRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponLogTypeRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;

/**
 * 쿠폰 로그 서비스.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CouponLogService {
    private final CouponLogRepository couponLogRepository;
    private final CouponLogTypeRepository couponLogTypeRepository;
    private final OrderRepository orderRepository;

    /**
     * 쿠폰 사용 취소.
     *
     * @param orderCouponCode the order coupon code
     */
    public void cancelOrderCoupon(UUID orderCouponCode) {
        Order order = orderRepository.findById(orderCouponCode)
            .orElseThrow(OrderNotFoundException::new);

        CouponLog couponLog = couponLogRepository.findByOrder(order)
            .orElseThrow(CouponLogNotFoundException::new);

        IssueCoupon issueCoupon = couponLog.getIssueCoupon();

        updateCouponLogType(issueCoupon, CouponLogType.Code.CANCEL);
    }

    private void updateCouponLogType(IssueCoupon issueCoupon, CouponLogType.Code couponLogTypeCode) {
        CouponLog couponLog = couponLogRepository.findTopByIssueCouponOrderByIdDesc(issueCoupon)
            .orElseThrow(CouponLogNotFoundException::new);

        CouponLogType couponLogType = couponLogTypeRepository.findById(couponLogTypeCode.toString())
            .orElseThrow(CouponLogTypeNotFoundException::new);

        couponLog.updateCouponLogType(couponLogType);
    }
}
