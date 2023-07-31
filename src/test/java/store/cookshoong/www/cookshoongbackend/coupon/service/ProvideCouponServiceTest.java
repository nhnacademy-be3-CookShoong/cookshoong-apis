package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockInterruptedException;
import store.cookshoong.www.cookshoongbackend.rabbitmq.exception.LockOverWaitTimeException;
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
    RedissonClient redissonClient;

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

        when(issueCouponRepository.provideCouponToAccount(any(UUID.class), any(LocalDate.class), any(Account.class)))
            .thenReturn(true);

        assertDoesNotThrow(() -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 쿠폰 정책 미존재")
    void provideCouponToAccountByEventNonPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 삭제된 쿠폰 정책")
    void provideCouponToAccountByEventDeletedPolicyFailTest() throws Exception {
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
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        RLock lock = mock(RLock.class);

        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
            .thenReturn(true);

        when(redissonClient.getLock(anyString()))
            .thenReturn(lock);

        assertThrowsExactly(CouponExhaustionException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 이미 발급한 기록이 있음")
    void provideCouponToAccountByEventIssueAlreadyFailTest() throws Exception {
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
    @DisplayName("쿠폰 이벤트 발급 실패 - lock 시간 제한")
    void provideCouponToAccountByEventLockTimeoutFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        RLock lock = mock(RLock.class);

        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
            .thenReturn(false);

        when(redissonClient.getLock(anyString()))
            .thenReturn(lock);

        assertThrowsExactly(LockOverWaitTimeException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 실패 - 인터럽트 발생")
    void provideCouponToAccountByEventInterruptFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        RLock lock = mock(RLock.class);

        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
            .thenThrow(InterruptedException.class);

        when(redissonClient.getLock(anyString()))
            .thenReturn(lock);

        assertThrowsExactly(LockInterruptedException.class,
            () -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }


    @Test
    @DisplayName("쿠폰 이벤트 발급 성공")
    void provideCouponToAccountByEventSuccessTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(anyLong()))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.getReferenceById(anyLong()))
            .thenReturn(customer);

        RLock lock = mock(RLock.class);

        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
            .thenReturn(true);

        when(redissonClient.getLock(anyString()))
            .thenReturn(lock);

        when(issueCouponRepository.findByCouponPolicyAndAccountIsNull(any(CouponPolicy.class)))
            .thenReturn(Optional.of(issueCoupon));

        assertDoesNotThrow(() -> provideCouponService.provideCouponToAccountByEvent(updateProvideCouponRequestDto));
    }
}
