package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponUsageNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypePercentRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageAllRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageMerchantRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageStoreRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@ExtendWith(MockitoExtension.class)
class CouponPolicyServiceTest {
    @InjectMocks
    CouponPolicyService couponPolicyService;
    @Mock
    CouponTypeCashRepository couponTypeCashRepository;
    @Mock
    CouponTypePercentRepository couponTypePercentRepository;
    @Mock
    CouponUsageStoreRepository couponUsageStoreRepository;
    @Mock
    CouponUsageMerchantRepository couponUsageMerchantRepository;
    @Mock
    CouponUsageAllRepository couponUsageAllRepository;
    @Mock
    CouponPolicyRepository couponPolicyRepository;
    @Mock
    StoreRepository storeRepository;
    @Mock
    MerchantRepository merchantRepository;

    TestEntity te = new TestEntity();

    AtomicInteger atomicInt = new AtomicInteger();
    AtomicLong atomicLong = new AtomicLong();

    Account account = persist(
        te.getAccount(te.getAccountStatusActive(), te.getAuthorityCustomer(), te.getRankLevelOne()));

    @Test
    @DisplayName("매장 금액 쿠폰 정책 생성 성공")
    void createStoreCashCouponPolicyTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto = te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "매장 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "해당 매장에서만 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        Store store = te.getStore(te.getMerchant(), account, te.getBankTypeKb(), te.getStoreStatusOpen());

        when(couponUsageStoreRepository.findByStoreId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Store persistStore = persist(store, id);
                return Optional.of(new CouponUsageStore(persistStore));
            });

        couponPolicyService.createStoreCashCouponPolicy(Long.MIN_VALUE, requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("매장 금액 쿠폰 정책 생성 실패 - 매장 사용처 없음")
    void createStoreCashCouponPolicyNonStoreUsageTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto = te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "매장 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "해당 매장에서만 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageStoreRepository.findByStoreId(anyLong()))
            .thenReturn(Optional.empty());

        Store store = te.getStore(te.getMerchant(), account, te.getBankTypeKb(), te.getStoreStatusOpen());

        when(storeRepository.getReferenceById(any(Long.class)))
            .thenReturn(persist(store));

        when(couponUsageStoreRepository.save(any(CouponUsageStore.class)))
            .thenAnswer(invocation -> persist(invocation.getArgument(0)));

        couponPolicyService.createStoreCashCouponPolicy(Long.MIN_VALUE, requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("매장 포인트 쿠폰 정책 생성")
    void createStorePointCouponPolicyTest() throws Exception {
        CreatePercentCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreatePercentCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "매장 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "해당 매장에서만 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "rate", new BigDecimal("10.0"));
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumPrice", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
            any(BigDecimal.class), anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(new CouponTypePercent(
                invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

        Store store = te.getStore(te.getMerchant(), account, te.getBankTypeKb(), te.getStoreStatusOpen());

        when(couponUsageStoreRepository.findByStoreId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Store persistStore = persist(store, id);
                return Optional.of(new CouponUsageStore(persistStore));
            });

        couponPolicyService.createStorePercentCouponPolicy(Long.MIN_VALUE, requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("가맹점 금액 쿠폰 정책 생성")
    void createMerchantCashCouponPolicyTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "가맹점 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "가맹점 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageMerchantRepository.findByMerchantId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Merchant merchant = persist(te.getMerchant(), id);
                return Optional.of(new CouponUsageMerchant(merchant));
            });

        couponPolicyService.createMerchantCashCouponPolicy(Long.MIN_VALUE, requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("가맹점 금액 쿠폰 정책 생성 실패 - 가맹점 사용처 없음")
    void createStoreCashCouponPolicyNonMerchantUsageTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "가맹점 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "가맹점 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageMerchantRepository.findByMerchantId(anyLong()))
            .thenReturn(Optional.empty());

        when(merchantRepository.getReferenceById(any(Long.class)))
            .thenReturn(persist(te.getMerchant()));

        when(couponUsageMerchantRepository.save(any(CouponUsageMerchant.class)))
            .thenAnswer(invocation -> persist(invocation.getArgument(0)));

        couponPolicyService.createMerchantCashCouponPolicy(Long.MIN_VALUE, requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("가맹점 포인트 쿠폰 정책 생성")
    void createMerchantPercentCouponPolicyTest() throws Exception {
        CreatePercentCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreatePercentCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "가맹점 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "가맹점 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "rate", new BigDecimal("10.0"));
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumPrice", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
            any(BigDecimal.class), anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(new CouponTypePercent(
                invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

        when(couponUsageMerchantRepository.findByMerchantId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Merchant merchant = persist(te.getMerchant(), id);
                return Optional.of(new CouponUsageMerchant(merchant));
            });
        couponPolicyService.createMerchantPercentCouponPolicy(Long.MIN_VALUE, requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("전체 금액 쿠폰 정책 생성")
    void createAllCashCouponPolicyTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "전체 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "모든 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(persist(new CouponUsageAll())));

        couponPolicyService.createAllCashCouponPolicy(requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("전체 포인트 쿠폰 정책 생성")
    void createAllPercentCouponPolicyTest() throws Exception {
        CreatePercentCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreatePercentCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "전체 포인트 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "모든 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "rate", new BigDecimal("10.0"));
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumPrice", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
            any(BigDecimal.class), anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(new CouponTypePercent(
                invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(persist(new CouponUsageAll())));

        couponPolicyService.createAllPercentCouponPolicy(requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("전체 금액 쿠폰 정책 생성 - 쿠폰 타입 없음")
    void createAllCashCouponPolicyNonTypeTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "전체 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "모든 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumPrice(anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        when(couponTypeCashRepository.save(any(CouponTypeCash.class)))
            .thenAnswer(invocation -> persist(invocation.getArgument(0)));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(persist(new CouponUsageAll())));

        couponPolicyService.createAllCashCouponPolicy(requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("전체 포인트 쿠폰 정책 생성 - 쿠폰 타입 없음")
    void createAllPercentCouponPolicyNonTypeTest() throws Exception {
        CreatePercentCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreatePercentCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "전체 포인트 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "모든 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "rate", new BigDecimal("10.0"));
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumPrice", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
            any(BigDecimal.class), anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        when(couponTypePercentRepository.save(any(CouponTypePercent.class)))
            .thenAnswer(invocation -> persist(invocation.getArgument(0)));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(persist(new CouponUsageAll())));

        couponPolicyService.createAllPercentCouponPolicy(requestDto);
        verify(couponPolicyRepository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("전체 포인트 쿠폰 정책 생성 실패 - 쿠폰 타입 없음")
    void createAllPercentCouponPolicyNonUsageTest() throws Exception {
        CreatePercentCouponPolicyRequestDto requestDto =
            te.createUsingDeclared(CreatePercentCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "전체 포인트 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "모든 매장에서 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(requestDto, "rate", new BigDecimal("10.0"));
        ReflectionTestUtils.setField(requestDto, "minimumPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumPrice", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumPriceAndMaximumPrice(
            any(BigDecimal.class), anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        when(couponTypePercentRepository.save(any(CouponTypePercent.class)))
            .thenAnswer(invocation -> persist(invocation.getArgument(0)));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.empty());

        assertThatThrownBy(() -> couponPolicyService.createAllPercentCouponPolicy(requestDto))
            .isInstanceOf(CouponUsageNotFoundException.class);
    }

    private <T> T persist(T t) {
        try {
            ReflectionTestUtils.setField(t, "id", atomicLong.getAndIncrement());
            return t;
        } catch (IllegalArgumentException e) {
            ReflectionTestUtils.setField(t, "id", atomicInt.getAndIncrement());
            return t;
        }
    }

    private <T> T persist(T t, long id) {
        ReflectionTestUtils.setField(t, "id", id);
        return t;
    }
}
