package store.cookshoong.www.cookshoongbackend.coupon.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.coupon.model.event.CouponOrderAbortEvent;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponLogService;

/**
 * 주문 중지에 의한 쿠폰 사용 취소 이벤트 리스너.
 *
 * @author eora21 (김주호)
 * @since 2023.08.09
 */
@Component
@RequiredArgsConstructor
public class CouponOrderAbortEventListener implements ApplicationListener<CouponOrderAbortEvent> {
    private final CouponLogService couponLogService;

    @Async
    @Override
    public void onApplicationEvent(CouponOrderAbortEvent event) {
        couponLogService.cancelOrderCoupon(event.getOrderCode());
    }
}
