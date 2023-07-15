package store.cookshoong.www.cookshoongbackend.coupon.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.coupon.entity.*;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponUsageNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.SelectPolicyResponseTempDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeCashVo;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypePercentVo;
import store.cookshoong.www.cookshoongbackend.coupon.repository.*;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Spy
    CouponTypeConverter couponTypeConverter;

    TestEntity te = new TestEntity();

    AtomicInteger atomicInt = new AtomicInteger();
    AtomicLong atomicLong = new AtomicLong();

    Account account = persist(
        te.getAccount(te.getAccountStatusActive(), te.getAuthorityCustomer(), te.getRankLevelOne()));

    @Test
    @DisplayName("매장 정책 확인")
    void selectStorePolicyTest() throws Exception {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();

        SelectPolicyResponseTempDto storeCashPolicy = new SelectPolicyResponseTempDto(
            atomicLong.getAndIncrement(), couponTypeCash, "매장 금액 쿠폰", "매장에서만 쓰입니다.",
            LocalTime.of(1, 0, 0));

        SelectPolicyResponseTempDto storePercentPolicy = new SelectPolicyResponseTempDto(
            atomicLong.getAndIncrement(), couponTypePercent, "매장 금액 쿠폰", "매장에서만 쓰입니다.",
            LocalTime.of(1, 0, 0));

        List<SelectPolicyResponseTempDto> couponStorePolicies = List.of(storeCashPolicy, storePercentPolicy);

        when(couponPolicyRepository.lookupStorePolicy(any(Long.class), any(Pageable.class)))
            .thenAnswer(invocation ->
                new PageImpl<>(couponStorePolicies, invocation.getArgument(1), couponStorePolicies.size()));

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyService.selectStorePolicy(Long.MIN_VALUE, Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(couponStorePolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<SelectPolicyResponseTempDto> tempIterator = couponStorePolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            SelectPolicyResponseTempDto temp = tempIterator.next();

            assertThat(response.getId()).isEqualTo(temp.getId());
            assertThat(response.getCouponTypeResponse())
                .isInstanceOfAny(CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(temp.getName());
            assertThat(response.getDescription()).isEqualTo(temp.getDescription());
            assertThat(response.getExpirationTime()).isEqualTo(temp.getExpirationTime());
        }
    }

    @Test
    @DisplayName("가맹점 정책 확인")
    void selectMerchantPolicyTest() throws Exception {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();

        SelectPolicyResponseTempDto merchantCashPolicy = new SelectPolicyResponseTempDto(
            atomicLong.getAndIncrement(), couponTypeCash, "가맹점 금액 쿠폰", "가맹점에서 쓰입니다.",
            LocalTime.of(1, 0, 0));

        SelectPolicyResponseTempDto merchantPercentPolicy = new SelectPolicyResponseTempDto(
            atomicLong.getAndIncrement(), couponTypePercent, "가맹점 금액 쿠폰", "가맹점에서 쓰입니다.",
            LocalTime.of(1, 0, 0));

        List<SelectPolicyResponseTempDto> couponMerchantPolicies = List.of(merchantCashPolicy, merchantPercentPolicy);

        when(couponPolicyRepository.lookupMerchantPolicy(any(Long.class), any(Pageable.class)))
            .thenAnswer(invocation ->
                new PageImpl<>(couponMerchantPolicies, invocation.getArgument(1), couponMerchantPolicies.size()));

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyService.selectMerchantPolicy(Long.MIN_VALUE, Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(couponMerchantPolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<SelectPolicyResponseTempDto> tempIterator = couponMerchantPolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            SelectPolicyResponseTempDto temp = tempIterator.next();

            assertThat(response.getId()).isEqualTo(temp.getId());
            assertThat(response.getCouponTypeResponse())
                .isInstanceOfAny(CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(temp.getName());
            assertThat(response.getDescription()).isEqualTo(temp.getDescription());
            assertThat(response.getExpirationTime()).isEqualTo(temp.getExpirationTime());
        }
    }

    @Test
    @DisplayName("사용처 전체 정책 확인")
    void selectAllPolicyTest() throws Exception {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();

        SelectPolicyResponseTempDto allCashPolicy = new SelectPolicyResponseTempDto(
            atomicLong.getAndIncrement(), couponTypeCash, "전체 금액 쿠폰", "어디든",
            LocalTime.of(1, 0, 0));

        SelectPolicyResponseTempDto allPercentPolicy = new SelectPolicyResponseTempDto(
            atomicLong.getAndIncrement(), couponTypePercent, "전체 금액 쿠폰", "어디든",
            LocalTime.of(1, 0, 0));

        List<SelectPolicyResponseTempDto> couponAllPolicies = List.of(allCashPolicy, allPercentPolicy);

        when(couponPolicyRepository.lookupAllPolicy(any(Pageable.class)))
            .thenAnswer(invocation ->
                new PageImpl<>(couponAllPolicies, invocation.getArgument(0), couponAllPolicies.size()));

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyService.selectUsageAllPolicy(Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(couponAllPolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<SelectPolicyResponseTempDto> tempIterator = couponAllPolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            SelectPolicyResponseTempDto temp = tempIterator.next();

            assertThat(response.getId()).isEqualTo(temp.getId());
            assertThat(response.getCouponTypeResponse())
                .isInstanceOfAny(CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(temp.getName());
            assertThat(response.getDescription()).isEqualTo(temp.getDescription());
            assertThat(response.getExpirationTime()).isEqualTo(temp.getExpirationTime());
        }
    }

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
