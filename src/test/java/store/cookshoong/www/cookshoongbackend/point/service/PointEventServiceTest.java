package store.cookshoong.www.cookshoongbackend.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.NonUniqueResultException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.utils.test.TestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailResponseDto;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderDetailRepository;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.repository.charge.ChargeRepository;
import store.cookshoong.www.cookshoongbackend.point.entity.PointLog;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReasonOrder;
import store.cookshoong.www.cookshoongbackend.point.exception.OrderPointLogDuplicateException;
import store.cookshoong.www.cookshoongbackend.point.repository.PointLogRepository;
import store.cookshoong.www.cookshoongbackend.point.repository.PointReasonOrderRepository;
import store.cookshoong.www.cookshoongbackend.point.repository.PointReasonRepository;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewHasImage;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.repository.ReviewRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@ExtendWith(MockitoExtension.class)
class PointEventServiceTest {
    @InjectMocks
    PointEventService pointEventService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    PointReasonRepository pointReasonRepository;
    @Mock
    PointReasonOrderRepository pointReasonOrderRepository;
    @Mock
    PointLogRepository pointLogRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderDetailRepository orderDetailRepository;
    @Mock
    ChargeRepository chargeRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Spy
    TestEntity te;

    List<LookupOrderDetailResponseDto> details;

    int totalCost;

    @BeforeEach
    void beforeEach() {
        details = List.of(
            new LookupOrderDetailResponseDto(1_000, 1, new BigDecimal("1.0")),
            new LookupOrderDetailResponseDto(1_500, 2, new BigDecimal("2.0")),
            new LookupOrderDetailResponseDto(2_000, 3, null)
        );

        totalCost = 0;

        for (LookupOrderDetailResponseDto detail : details) {
            totalCost += detail.getNowCost() * detail.getCount();
        }
    }

    @Test
    @DisplayName("회원가입 포인트 제공 성공")
    void createSignupPointAccountSuccessTest() throws Exception {
        assertDoesNotThrow(() ->
            pointEventService.createSignupPoint(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("주문 포인트 제공 실패 - 주문 정보 없음")
    void createOrderPointOrderNotFoundFailTest() throws Exception {
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class, () ->
            pointEventService.createPaymentPoint(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 포인트 제공 성공")
    void createOrderPointSuccessTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        Account account = mock(Account.class);
        when(order.getAccount())
            .thenReturn(account);

        Store store = mock(Store.class);
        when(order.getStore())
            .thenReturn(store);

        BigDecimal defaultEarningRate = new BigDecimal("3.0");
        when(store.getDefaultEarningRate())
            .thenReturn(defaultEarningRate);

        when(orderDetailRepository.lookupOrderDetailForPoint(any(UUID.class)))
            .thenReturn(details);

        when(pointLogRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Charge charge = mock(Charge.class);
        when(chargeRepository.findByOrder(any(Order.class)))
            .thenReturn(Optional.of(charge));

        when(charge.getChargedAmount())
            .thenReturn(totalCost);

        assertDoesNotThrow(() ->
            pointEventService.createPaymentPoint(UUID.randomUUID()));

        ArgumentCaptor<PointLog> pointLogCaptor = ArgumentCaptor.forClass(PointLog.class);
        verify(pointLogRepository).save(pointLogCaptor.capture());

        int point = pointLogCaptor.getValue().getPointMovement();

        int testPoint = 0;
        for (LookupOrderDetailResponseDto detail : details) {
            testPoint += (int) (detail.getNowCost() * detail.getCount() / 100 * detail.getEarningRate());
        }

        assertThat(point).isEqualTo(testPoint);
    }

    @Test
    @DisplayName("주문 포인트 제공 성공 - 할인값")
    void createOrderPointDiscountAmountSuccessTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        Account account = mock(Account.class);
        when(order.getAccount())
            .thenReturn(account);

        Store store = mock(Store.class);
        when(order.getStore())
            .thenReturn(store);

        BigDecimal defaultEarningRate = new BigDecimal("3.0");
        when(store.getDefaultEarningRate())
            .thenReturn(defaultEarningRate);

        when(orderDetailRepository.lookupOrderDetailForPoint(any(UUID.class)))
            .thenReturn(details);

        when(pointLogRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Charge charge = mock(Charge.class);
        when(chargeRepository.findByOrder(any(Order.class)))
            .thenReturn(Optional.of(charge));

        double afterDiscountPercent = 0.8;

        when(charge.getChargedAmount())
            .thenReturn((int) (totalCost * afterDiscountPercent));

        assertDoesNotThrow(() ->
            pointEventService.createPaymentPoint(UUID.randomUUID()));

        ArgumentCaptor<PointLog> pointLogCaptor = ArgumentCaptor.forClass(PointLog.class);
        verify(pointLogRepository).save(pointLogCaptor.capture());

        int point = pointLogCaptor.getValue().getPointMovement();

        int testPoint = 0;
        for (LookupOrderDetailResponseDto detail : details) {
            testPoint += (int) (detail.getNowCost() * detail.getCount() / 100 * detail.getEarningRate());
        }

        int expected = (int) (testPoint * afterDiscountPercent);
        assertThat(point).isEqualTo(expected);
    }

    @Test
    @DisplayName("리뷰 포인트 생성 실패 - 리뷰 없음")
    void createReviewPointNonReviewFailTest() throws Exception {
        when(reviewRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(ReviewNotFoundException.class, () ->
            pointEventService.createReviewPoint(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("리뷰 포인트 생성 성공 - 리뷰 사진 없음")
    void createReviewPointNonImageSuccessTest() throws Exception {
        int reviewDefaultPoint =
            TestUtils.getPropertyValue(pointEventService, "REVIEW_DEFAULT_POINT", Integer.class);

        Review review = mock(Review.class);
        when(reviewRepository.findById(anyLong()))
            .thenReturn(Optional.of(review));

        ArgumentCaptor<PointLog> captor = ArgumentCaptor.forClass(PointLog.class);

        assertDoesNotThrow(() ->
            pointEventService.createReviewPoint(Long.MIN_VALUE));

        verify(pointLogRepository).save(captor.capture());

        assertThat(captor.getValue().getPointMovement()).isEqualTo(reviewDefaultPoint);
    }

    @Test
    @DisplayName("리뷰 포인트 생성 성공 - 리뷰 사진 있음")
    void createReviewPointExistImageSuccessTest() throws Exception {
        Integer reviewImagePoint =
            TestUtils.getPropertyValue(pointEventService, "REVIEW_IMAGE_POINT", Integer.class);

        Review review = mock(Review.class);
        when(reviewRepository.findById(anyLong()))
            .thenReturn(Optional.of(review));

        when(review.getReviewHasImages())
            .thenReturn(Set.of(mock(ReviewHasImage.class)));

        ArgumentCaptor<PointLog> captor = ArgumentCaptor.forClass(PointLog.class);

        assertDoesNotThrow(() ->
            pointEventService.createReviewPoint(Long.MIN_VALUE));

        verify(pointLogRepository).save(captor.capture());

        assertThat(captor.getValue().getPointMovement()).isEqualTo(reviewImagePoint);
    }

    @Test
    @DisplayName("주문 중단 포인트 환불 실패 - 주문 없음")
    void refundOrderPointNonOrderFailTest() throws Exception {
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class, () ->
            pointEventService.refundOrderPoint(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 중단 포인트 환불 실패 - 포인트 중복")
    void refundOrderPointDuplicateOrderFailTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        when(pointLogRepository.lookupUsePoint(order))
            .thenThrow(NonUniqueResultException.class);

        assertThrowsExactly(OrderPointLogDuplicateException.class, () ->
            pointEventService.refundOrderPoint(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 중단 포인트 환불 - 사용 포인트 없음")
    void refundOrderPointNullTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        when(pointLogRepository.lookupUsePoint(order))
            .thenReturn(null);

        assertDoesNotThrow(() ->
            pointEventService.refundOrderPoint(UUID.randomUUID()));

        verify(pointLogRepository, never()).save(any(PointLog.class));
    }

    @Test
    @DisplayName("주문 중단 포인트 환불 성공")
    void refundOrderPointSuccessTest() throws Exception {
        Order order = mock(Order.class);
        when(orderRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(order));

        PointLog pointLog = mock(PointLog.class);
        when(pointLogRepository.lookupUsePoint(order))
            .thenReturn(pointLog);

        Account account = mock(Account.class);
        when(pointLog.getAccount())
            .thenReturn(account);

        when(pointLog.getPointMovement())
            .thenReturn(-1_000);

        assertDoesNotThrow(() ->
            pointEventService.refundOrderPoint(UUID.randomUUID()));

        verify(pointLogRepository).save(any(PointLog.class));
    }
}
