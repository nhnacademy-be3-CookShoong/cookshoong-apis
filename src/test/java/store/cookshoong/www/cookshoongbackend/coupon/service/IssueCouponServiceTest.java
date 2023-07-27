package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyHasCouponWithinSamePolicyException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequest;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@ExtendWith(MockitoExtension.class)
class IssueCouponServiceTest {
    @InjectMocks
    IssueCouponService issueCouponService;
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

    CreateIssueCouponRequestDto createIssueCouponRequestDto;

    UpdateProvideCouponRequest updateProvideCouponRequest;

    @BeforeEach
    void beforeEach() {
        createIssueCouponRequestDto = te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", 1L);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "couponPolicyId", Long.MIN_VALUE);

        updateProvideCouponRequest = te.createUsingDeclared(UpdateProvideCouponRequest.class);
        ReflectionTestUtils.setField(updateProvideCouponRequest, "couponPolicyId", Long.MIN_VALUE);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 1000})
    @DisplayName("매장 쿠폰 발행 성공")
    void createStoreIssueCouponSuccessTest(int issueQuantity) throws Exception {

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueQuantity);

        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        when(issueCouponRepository.save(any()))
            .thenReturn(issueCoupon);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        verify(issueCouponRepository, times(issueQuantity)).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 1000})
    @DisplayName("가맹점 쿠폰 발행 성공")
    void createMerchantIssueCouponSuccessTest(int issueQuantity) throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageMerchant(te.getMerchant())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueQuantity);

        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        when(issueCouponRepository.save(any()))
            .thenReturn(issueCoupon);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        verify(issueCouponRepository, times(issueQuantity)).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 1000})
    @DisplayName("전체 쿠폰 발행 성공")
    void createUsageAllIssueCouponSuccessTest(int issueQuantity) throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll()));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueQuantity);

        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        when(issueCouponRepository.save(any()))
            .thenReturn(issueCoupon);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        verify(issueCouponRepository, times(issueQuantity)).save(any());
    }

    @Test
    @DisplayName("쿠폰 발행 실패 - 쿠폰 정책 미존재")
    void createIssueCouponNonCouponPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> issueCouponService.createIssueCoupon(createIssueCouponRequestDto));
    }

    @ParameterizedTest
    @ValueSource(classes = {CouponUsageAll.class, CouponUsageMerchant.class, CouponUsageStore.class})
    @DisplayName("쿠폰 발행 실패 - 유효 쿠폰 개수 초과")
    void createIssueCouponOverLimitFailTest(Class<? extends CouponUsage> couponUsageClass) throws Exception {
        CouponUsage couponUsage = mock(couponUsageClass);
        when(couponUsage.limitCount())
            .thenReturn(OptionalInt.of(Integer.MIN_VALUE));

        CouponPolicy couponPolicy =
            te.persist(te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), couponUsage));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        assertThrowsExactly(IssueCouponOverCountException.class,
            () -> issueCouponService.createIssueCoupon(createIssueCouponRequestDto));
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void provideCouponToAccountSuccessTest() throws Exception {
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

        when(issueCouponRepository.provideCouponToAccount(any(UUID.class), any(LocalDate.class), any(Account.class)))
            .thenReturn(true);

        assertDoesNotThrow(() -> issueCouponService.provideCouponToAccount(Long.MIN_VALUE, updateProvideCouponRequest));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 정책 미존재")
    void provideCouponToAccountNonPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> issueCouponService.provideCouponToAccount(Long.MIN_VALUE, updateProvideCouponRequest));
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
            () -> issueCouponService.provideCouponToAccount(Long.MIN_VALUE, updateProvideCouponRequest));
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
            () -> issueCouponService.provideCouponToAccount(Long.MIN_VALUE, updateProvideCouponRequest));
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

        when(issueCouponRepository
            .provideCouponToAccount(any(UUID.class), any(LocalDate.class), any(Account.class)))
            .thenReturn(false);

        assertThrowsExactly(ProvideIssueCouponFailureException.class,
            () -> issueCouponService.provideCouponToAccount(Long.MIN_VALUE, updateProvideCouponRequest));
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
            () -> issueCouponService.provideCouponToAccount(Long.MIN_VALUE, updateProvideCouponRequest));
    }
}
