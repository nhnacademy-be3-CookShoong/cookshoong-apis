package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
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
        when(couponPolicyRepository.findById(any(Long.class)))
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

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 없음")
    void provideCouponToAccountNonIssueCouponsFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(any(CouponPolicy.class)))
            .thenReturn(Collections.emptyList());

        assertThrowsExactly(IssueCouponNotFoundException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 경쟁 밀림")
    void provideCouponToAccountIssueFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.getReferenceById(any(Long.class)))
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

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.isReceivedBefore(any(Long.class), any(Long.class), any(Integer.class)))
            .thenReturn(true);

        assertThrowsExactly(AlreadyHasCouponWithinSamePolicyException.class,
            () -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }


    @Test
    @DisplayName("쿠폰 발급 성공")
    void provideCouponToAccountSuccessTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        IssueCoupon issueCoupon = new IssueCoupon(couponPolicy);
        ReflectionTestUtils.setField(issueCoupon, "code", UUID.randomUUID());

        when(issueCouponRepository.findTop10ByCouponPolicyAndAccountIsNull(any(CouponPolicy.class)))
            .thenReturn(List.of(issueCoupon));

        Account customer = tpe.getLevelOneActiveCustomer();
        when(accountRepository.getReferenceById(any(Long.class)))
            .thenReturn(customer);

        when(issueCouponRepository.provideCouponToAccount(any(UUID.class), any(LocalDate.class), any(Account.class)))
            .thenReturn(true);

        assertDoesNotThrow(() -> provideCouponService.provideCouponToAccountByApi(updateProvideCouponRequestDto));
    }
}
