package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class CouponLogServiceTest {
    @InjectMocks
    CouponLogService couponLogService;
    @Mock
    CouponLogRepository couponLogRepository;
    @Mock
    CouponLogTypeRepository couponLogTypeRepository;
    @Mock
    OrderRepository orderRepository;

    @Test
    @DisplayName("쿠폰 사용 취소 실패 - 주문 미확인")
    void cancelOrderCouponOrderNotFoundFailTest() throws Exception {
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class, () ->
            couponLogService.cancelOrderCoupon(UUID.randomUUID()));
    }

    @Test
    @DisplayName("쿠폰 사용 취소 실패 - 쿠폰 로그 미확인")
    void cancelOrderCouponCouponLogNotFoundFailTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        when(couponLogRepository.findByOrder(order))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponLogNotFoundException.class, () ->
            couponLogService.cancelOrderCoupon(UUID.randomUUID()));
    }

    @Test
    @DisplayName("쿠폰 사용 취소 실패 - 발행 쿠폰 로그 없음")
    void cancelOrderCouponIssueCouponLogNotFoundFailTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        CouponLog couponLog = mock(CouponLog.class);
        when(couponLogRepository.findByOrder(order))
            .thenReturn(Optional.of(couponLog));

        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(couponLog.getIssueCoupon())
            .thenReturn(issueCoupon);

        when(couponLogRepository.findTopByIssueCouponOrderByIdDesc(issueCoupon))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponLogNotFoundException.class, () ->
            couponLogService.cancelOrderCoupon(UUID.randomUUID()));
    }

    @Test
    @DisplayName("쿠폰 사용 취소 실패 - 쿠폰 로그 타입 없음")
    void cancelOrderCouponCouponLogTypeNotFoundFailTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        CouponLog couponLog = mock(CouponLog.class);
        when(couponLogRepository.findByOrder(order))
            .thenReturn(Optional.of(couponLog));

        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(couponLog.getIssueCoupon())
            .thenReturn(issueCoupon);

        CouponLog recentCouponLog = mock(CouponLog.class);

        when(couponLogRepository.findTopByIssueCouponOrderByIdDesc(issueCoupon))
            .thenReturn(Optional.of(recentCouponLog));

        when(couponLogTypeRepository.findByCouponLogTypeCode(any(CouponLogType.Code.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponLogTypeNotFoundException.class, () ->
            couponLogService.cancelOrderCoupon(UUID.randomUUID()));
    }

    @Test
    @DisplayName("쿠폰 사용 취소 성공")
    void cancelOrderCouponSuccessTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        CouponLog couponLog = mock(CouponLog.class);
        when(couponLogRepository.findByOrder(order))
            .thenReturn(Optional.of(couponLog));

        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(couponLog.getIssueCoupon())
            .thenReturn(issueCoupon);

        CouponLog recentCouponLog = spy(CouponLog.class);

        when(couponLogRepository.findTopByIssueCouponOrderByIdDesc(issueCoupon))
            .thenReturn(Optional.of(recentCouponLog));

        CouponLogType couponLogType = mock(CouponLogType.class);
        when(couponLogTypeRepository.findByCouponLogTypeCode(any(CouponLogType.Code.class)))
            .thenReturn(Optional.of(couponLogType));

        assertDoesNotThrow(() ->
            couponLogService.cancelOrderCoupon(UUID.randomUUID()));

        verify(recentCouponLog).updateCouponLogType(couponLogType);
        assertThat(recentCouponLog.getCouponLogType())
            .isEqualTo(couponLogType);
    }
}
