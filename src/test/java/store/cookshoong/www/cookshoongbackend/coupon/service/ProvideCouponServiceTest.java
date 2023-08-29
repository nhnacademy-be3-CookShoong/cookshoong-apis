package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLog;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLogType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyUsedCouponException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ExpiredCouponException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.NonIssuedCouponProperlyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponLogRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponRedisRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.BelowMinimumOrderPriceException;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@ExtendWith(MockitoExtension.class)
class ProvideCouponServiceTest {
    @InjectMocks
    ProvideCouponService provideCouponService;
    @Mock
    IssueCouponRepository issueCouponRepository;
    @Mock
    CouponPolicyRepository couponPolicyRepository;
    @Spy
    TestEntity te;
    @InjectMocks
    TestPersistEntity tpe;
    @Mock
    AccountRepository accountRepository;
    @Mock
    CouponRedisRepository couponRedisRepository;
    @Mock
    CouponLogRepository couponLogRepository;
    @Mock
    LockProcessor lockProcessor;
    UpdateProvideCouponRequestDto updateProvideCouponRequestDto;

    @BeforeEach
    void beforeEach() {
        updateProvideCouponRequestDto = te.createUsingDeclared(UpdateProvideCouponRequestDto.class);
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, "couponPolicyId", Long.MIN_VALUE);
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, "accountId", Long.MIN_VALUE);
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 정책 미존재")
    void provideCouponToAccountNonPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 삭제된 쿠폰 정책")
    void provideCouponToAccountDeletedPolicyFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        couponPolicy.delete();

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 없음")
    void provideCouponToAccountNonIssueCouponsFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(any(CouponPolicy.class)))
            .thenReturn(Collections.emptyList());

        assertThrowsExactly(CouponExhaustionException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 경쟁 밀림")
    void provideCouponToAccountIssueFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.getReferenceById(anyLong()))
            .thenReturn(customer);

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(any(CouponPolicy.class)))
            .thenReturn(List.of(issueCoupon));

        doThrow(ProvideIssueCouponFailureException.class).
            when(issueCouponRepository).provideCouponToAccount(any(IssueCoupon.class), any(Account.class));


        assertThrowsExactly(ProvideIssueCouponFailureException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급한 기록이 있음")
    void provideCouponToAccountIssueAlreadyFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.isReceivedBefore(anyLong(), anyLong(), any(Integer.class)))
            .thenReturn(true);

        assertThrowsExactly(AlreadyHasCouponWithinSamePolicyException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }


    @Test
    @DisplayName("쿠폰 발급 성공")
    void provideCouponToAccountSuccessTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(any(CouponPolicy.class)))
            .thenReturn(List.of(issueCoupon));

        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.getReferenceById(anyLong()))
            .thenReturn(customer);

        assertDoesNotThrow(() -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 쿠폰 정책 미존재")
    void provideCouponToAccountByEventNonPolicyFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 삭제된 쿠폰 정책")
    void provideCouponToAccountByEventDeletedPolicyFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        couponPolicy.delete();

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 유효 쿠폰 없음")
    void provideCouponToAccountByEventNonIssueCouponsFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        BoundSetOperations<String, Object> mockSet = mock(BoundSetOperations.class);

        when(couponRedisRepository.getRedisSet(anyString()))
            .thenReturn(mockSet);

        when(mockSet.pop())
            .thenReturn(null);

        assertThrowsExactly(CouponExhaustionException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 이미 발급한 기록이 있음")
    void provideCouponToAccountByEventIssueAlreadyFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.isReceivedBefore(anyLong(), anyLong(), any(Integer.class)))
            .thenReturn(true);

        assertThrowsExactly(AlreadyHasCouponWithinSamePolicyException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 이미 발급된 쿠폰")
    void provideCouponToAccountByEventAlreadyIssueCouponFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        BoundSetOperations<String, Object> mockSet = mock(BoundSetOperations.class);

        when(couponRedisRepository.getRedisSet(anyString()))
            .thenReturn(mockSet);

        UUID randomUUID = UUID.randomUUID();
        when(mockSet.pop())
            .thenReturn(randomUUID.toString());

        IssueCoupon issueCoupon = mock(IssueCoupon.class);

        when(issueCouponRepository.findById(randomUUID))
            .thenReturn(Optional.of(issueCoupon));

        Account account = mock(Account.class);
        when(issueCoupon.getAccount())
            .thenReturn(account);

        assertThrowsExactly(ProvideIssueCouponFailureException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 성공")
    void provideCouponToAccountByEventSuccessTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        BoundSetOperations<String, Object> mockSet = mock(BoundSetOperations.class);

        when(couponRedisRepository.getRedisSet(anyString()))
            .thenReturn(mockSet);

        UUID randomUUID = UUID.randomUUID();
        when(mockSet.pop())
            .thenReturn(randomUUID.toString());

        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.getReferenceById(anyLong()))
            .thenReturn(customer);

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", randomUUID);

        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        assertDoesNotThrow(() -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("발급 쿠폰 검증 실패 - 쿠폰 없음")
    void validProvideCouponNotFoundFailTest() throws Exception {
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(IssueCouponNotFoundException.class, () ->
            provideCouponService.validProvideCoupon(UUID.randomUUID(), Long.MIN_VALUE));
    }

    @Test
    @DisplayName("발급 쿠폰 검증 실패 - 이미 사용한 쿠폰")
    void validProvideCouponAlreadyUseFailTest() throws Exception {
        IssueCoupon issueCoupon = tpe.getIssueCoupon();
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        CouponLogType couponLogType = mock(CouponLogType.class);
        when(couponLogType.getCode())
            .thenReturn("USE");

        CouponLog couponLog = mock(CouponLog.class);
        when(couponLog.getCouponLogType())
            .thenReturn(couponLogType);

        when(couponLogRepository.findTopByIssueCouponOrderByIdDesc(any(IssueCoupon.class)))
            .thenReturn(Optional.of(couponLog));

        assertThrowsExactly(AlreadyUsedCouponException.class, () ->
            provideCouponService.validProvideCoupon(UUID.randomUUID(), Long.MIN_VALUE));
    }

    @Test
    @DisplayName("발급 쿠폰 검증 실패 - 발급된 쿠폰이 아님")
    void validProvideCouponNonProvideFailTest() throws Exception {
        IssueCoupon issueCoupon = tpe.getIssueCoupon();
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        assertThrowsExactly(NonIssuedCouponProperlyException.class, () ->
            provideCouponService.validProvideCoupon(UUID.randomUUID(), Long.MIN_VALUE));
    }

    @Test
    @DisplayName("발급 쿠폰 검증 실패 - 다른 사용자에게 발급된 쿠폰")
    void validProvideCouponProvidedOtherAccountFailTest() throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        Account account = mock(Account.class);
        when(account.getId())
            .thenReturn(Long.MAX_VALUE);

        when(issueCoupon.getAccount())
            .thenReturn(account);

        assertThrowsExactly(NonIssuedCouponProperlyException.class, () ->
            provideCouponService.validProvideCoupon(UUID.randomUUID(), Long.MIN_VALUE));
    }

    @Test
    @DisplayName("발급 쿠폰 검증 성공")
    void validProvideCouponSuccessTest() throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        CouponLogType couponLogType = mock(CouponLogType.class);
        when(couponLogType.getCode())
            .thenReturn("REFUND");

        CouponLog couponLog = mock(CouponLog.class);
        when(couponLog.getCouponLogType())
            .thenReturn(couponLogType);

        when(couponLogRepository.findTopByIssueCouponOrderByIdDesc(any(IssueCoupon.class)))
            .thenReturn(Optional.of(couponLog));

        Account account = mock(Account.class);
        when(account.getId())
            .thenReturn(Long.MIN_VALUE);

        when(issueCoupon.getAccount())
            .thenReturn(account);

        assertDoesNotThrow(() ->
            provideCouponService.validProvideCoupon(UUID.randomUUID(), Long.MIN_VALUE));
    }

    @Test
    @DisplayName("쿠폰 최소 주문 금액 검증 실패 - 쿠폰 없음")
    void validMinimumOrderPriceNotFoundIssueCouponFailTest() throws Exception {
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(IssueCouponNotFoundException.class, () ->
            provideCouponService.validMinimumOrderPrice(UUID.randomUUID(), Integer.MAX_VALUE));
    }

    @ParameterizedTest
    @ValueSource(ints = {10_000, 20_000, Integer.MIN_VALUE})
    @DisplayName("쿠폰 최소 주문 금액 검증 실패 - 최소 주문 금액 미충족")
    void validMinimumOrderPriceBelowFailTest(int totalPrice) throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        CouponType couponType = mock(CouponType.class);
        when(couponType.getMinimumOrderPrice())
            .thenReturn(Integer.MAX_VALUE);

        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(couponPolicy.getCouponType())
            .thenReturn(couponType);

        when(issueCoupon.getCouponPolicy())
            .thenReturn(couponPolicy);

        assertThrowsExactly(BelowMinimumOrderPriceException.class, () ->
            provideCouponService.validMinimumOrderPrice(UUID.randomUUID(), totalPrice));
    }

    @ParameterizedTest
    @ValueSource(ints = {10_000, 20_000, Integer.MAX_VALUE})
    @DisplayName("쿠폰 최소 주문 금액 검증 성공")
    void validMinimumOrderPriceSuccessTest(int totalPrice) throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        CouponType couponType = mock(CouponType.class);
        when(couponType.getMinimumOrderPrice())
            .thenReturn(10_000);

        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(couponPolicy.getCouponType())
            .thenReturn(couponType);

        when(issueCoupon.getCouponPolicy())
            .thenReturn(couponPolicy);

        assertDoesNotThrow(() ->
            provideCouponService.validMinimumOrderPrice(UUID.randomUUID(), totalPrice));
    }

    @Test
    @DisplayName("쿠폰 만료 시 검증 실패 - 쿠폰 없음")
    void validExpirationDateTimeNotFoundIssueCouponFailTest() throws Exception {
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(IssueCouponNotFoundException.class, () ->
            provideCouponService.validExpirationDateTime(UUID.randomUUID()));
    }

    @Test
    @DisplayName("쿠폰 만료 시간 검증 실패 - 시간 초과")
    void validExpirationDateTimeOverFailTest() throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        when(issueCoupon.getExpirationDate())
            .thenReturn(LocalDate.now().minusDays(2));

        assertThrowsExactly(ExpiredCouponException.class, () ->
            provideCouponService.validExpirationDateTime(UUID.randomUUID()));
    }

    @Test
    @DisplayName("쿠폰 만료 시간 검증 성공")
    void validExpirationDateTimeSuccessTest() throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        when(issueCoupon.getExpirationDate())
            .thenReturn(LocalDate.now());

        assertDoesNotThrow(() ->
            provideCouponService.validExpirationDateTime(UUID.randomUUID()));
    }

    @Test
    @DisplayName("할인 금액 획득 실패 - 발행 쿠폰 없음")
    void getDiscountPriceIssueCouponNotFoundFailTest() throws Exception {
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(IssueCouponNotFoundException.class, () ->
            provideCouponService.getDiscountPrice(UUID.randomUUID(), 10_000));
    }

    @Test
    @DisplayName("고정 할인 금액 획득 성공")
    void getDiscountPriceIssueCouponCashSuccessTest() throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(issueCoupon.getCouponPolicy())
            .thenReturn(couponPolicy);

        CouponTypeCash couponTypeCash = new CouponTypeCash(1_000, 0);
        when(couponPolicy.getCouponType())
            .thenReturn(couponTypeCash);

        assertThat(provideCouponService.getDiscountPrice(UUID.randomUUID(), 10_000))
            .isEqualTo(9_000);
    }

    @Test
    @DisplayName("퍼센트 할인 금액 획득 성공")
    void getDiscountPriceIssueCouponPercentSuccessTest() throws Exception {
        IssueCoupon issueCoupon = mock(IssueCoupon.class);
        when(issueCouponRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(issueCoupon));

        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(issueCoupon.getCouponPolicy())
            .thenReturn(couponPolicy);

        CouponTypePercent couponTypePercent =
            new CouponTypePercent(10, 10_000, 0);

        when(couponPolicy.getCouponType())
            .thenReturn(couponTypePercent);

        assertThat(provideCouponService.getDiscountPrice(UUID.randomUUID(), 10_000))
            .isEqualTo(9_000);
    }

    @Test
    @DisplayName("발급 전 검증 실패 - 쿠폰 정책 없음")
    void validBeforeProvideNonPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class, () ->
        provideCouponService.validBeforeProvide(Long.MIN_VALUE, Long.MIN_VALUE));
    }

    @Test
    @DisplayName("발급 전 검증")
    void validBeforeProvideTest() throws Exception {
        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        assertDoesNotThrow(() ->
            provideCouponService.validBeforeProvide(Long.MIN_VALUE, Long.MIN_VALUE));
    }
}
