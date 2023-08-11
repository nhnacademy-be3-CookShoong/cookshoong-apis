package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponUsageNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableStoreCouponPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeCashVo;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypePercentVo;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypeCashRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponTypePercentRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageAllRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageMerchantRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponUsageStoreRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.CouponTypeConverter;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
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
    @Spy
    CouponTypeConverter couponTypeConverter;

    TestEntity te = new TestEntity();

    Account account = te.persist(
        te.getAccount(te.getAccountStatusActive(), te.getAuthorityCustomer(), te.getRankLevelOne()));

    @Test
    @DisplayName("매장 정책 확인")
    void selectStorePolicyTest() throws Exception {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();

        SelectPolicyResponseDto storeCashPolicy = new SelectPolicyResponseDto(
            te.getLong(), couponTypeCash, "매장 금액 쿠폰", "매장에서만 쓰입니다.",
            0, 1L, 10L);

        SelectPolicyResponseDto storePercentPolicy = new SelectPolicyResponseDto(
            te.getLong(), couponTypePercent, "매장 금액 쿠폰", "매장에서만 쓰입니다.",
            0, 2L, 9L);

        List<SelectPolicyResponseDto> couponStorePolicies = List.of(storeCashPolicy, storePercentPolicy);

        when(couponPolicyRepository.lookupStorePolicy(any(Long.class), any(Pageable.class)))
            .thenAnswer(invocation ->
                new PageImpl<>(couponStorePolicies, invocation.getArgument(1), couponStorePolicies.size()));

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyService.selectStorePolicy(Long.MIN_VALUE, Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(couponStorePolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<SelectPolicyResponseDto> tempIterator = couponStorePolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            SelectPolicyResponseDto temp = tempIterator.next();

            assertThat(response.getId()).isEqualTo(temp.getId());
            assertThat(response.getCouponTypeResponse())
                .isInstanceOfAny(CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(temp.getName());
            assertThat(response.getDescription()).isEqualTo(temp.getDescription());
            assertThat(response.getUsagePeriod()).isEqualTo(temp.getUsagePeriod());
            assertThat(response.getUnclaimedCouponCount()).isEqualTo(temp.getUnclaimedCouponCount());
            assertThat(response.getIssueCouponCount()).isEqualTo(temp.getIssueCouponCount());
        }
    }

    @Test
    @DisplayName("가맹점 정책 확인")
    void selectMerchantPolicyTest() throws Exception {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();

        SelectPolicyResponseDto merchantCashPolicy = new SelectPolicyResponseDto(
            te.getLong(), couponTypeCash, "가맹점 금액 쿠폰", "가맹점에서 쓰입니다.",
            0, 3L, 8L);

        SelectPolicyResponseDto merchantPercentPolicy = new SelectPolicyResponseDto(
            te.getLong(), couponTypePercent, "가맹점 금액 쿠폰", "가맹점에서 쓰입니다.",
            0, 4L, 7L);

        List<SelectPolicyResponseDto> couponMerchantPolicies = List.of(merchantCashPolicy, merchantPercentPolicy);

        when(couponPolicyRepository.lookupMerchantPolicy(any(Long.class), any(Pageable.class)))
            .thenAnswer(invocation ->
                new PageImpl<>(couponMerchantPolicies, invocation.getArgument(1), couponMerchantPolicies.size()));

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyService.selectMerchantPolicy(Long.MIN_VALUE, Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(couponMerchantPolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<SelectPolicyResponseDto> tempIterator = couponMerchantPolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            SelectPolicyResponseDto temp = tempIterator.next();

            assertThat(response.getId()).isEqualTo(temp.getId());
            assertThat(response.getCouponTypeResponse())
                .isInstanceOfAny(CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(temp.getName());
            assertThat(response.getDescription()).isEqualTo(temp.getDescription());
            assertThat(response.getUsagePeriod()).isEqualTo(temp.getUsagePeriod());
            assertThat(response.getUnclaimedCouponCount()).isEqualTo(temp.getUnclaimedCouponCount());
            assertThat(response.getIssueCouponCount()).isEqualTo(temp.getIssueCouponCount());
        }
    }

    @Test
    @DisplayName("사용처 전체 정책 확인")
    void selectAllPolicyTest() throws Exception {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();

        SelectPolicyResponseDto allCashPolicy = new SelectPolicyResponseDto(
            te.getLong(), couponTypeCash, "전체 금액 쿠폰", "어디든",
            0, 5L, 6L);

        SelectPolicyResponseDto allPercentPolicy = new SelectPolicyResponseDto(
            te.getLong(), couponTypePercent, "전체 금액 쿠폰", "어디든",
            0, 6L, 50L);

        List<SelectPolicyResponseDto> couponAllPolicies = List.of(allCashPolicy, allPercentPolicy);

        when(couponPolicyRepository.lookupAllPolicy(any(Pageable.class)))
            .thenAnswer(invocation ->
                new PageImpl<>(couponAllPolicies, invocation.getArgument(0), couponAllPolicies.size()));

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyService.selectUsageAllPolicy(Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(couponAllPolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<SelectPolicyResponseDto> tempIterator = couponAllPolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            SelectPolicyResponseDto temp = tempIterator.next();

            assertThat(response.getId()).isEqualTo(temp.getId());
            assertThat(response.getCouponTypeResponse())
                .isInstanceOfAny(CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(temp.getName());
            assertThat(response.getDescription()).isEqualTo(temp.getDescription());
            assertThat(response.getUsagePeriod()).isEqualTo(temp.getUsagePeriod());
            assertThat(response.getUnclaimedCouponCount()).isEqualTo(temp.getUnclaimedCouponCount());
            assertThat(response.getIssueCouponCount()).isEqualTo(temp.getIssueCouponCount());
        }
    }

    @Test
    @DisplayName("매장 금액 쿠폰 정책 생성 성공")
    void createStoreCashCouponPolicyTest() throws Exception {
        CreateCashCouponPolicyRequestDto requestDto = te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "매장 금액 쿠폰");
        ReflectionTestUtils.setField(requestDto, "description", "해당 매장에서만 쓰입니다.");
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        Image businessImage = te.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.BUSINESS_INFO_IMAGE.getVariable(), "사업자등록증.png", false);
        Image storeImage = te.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.STORE_IMAGE.getVariable(), "매장사진.png", true);
        Store store = te.getStore(te.getMerchant(), account, te.getBankTypeKb(), te.getStoreStatusOpen(), businessImage, storeImage);

        when(couponUsageStoreRepository.findByStoreId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Store persistStore = te.persist(store, id);
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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageStoreRepository.findByStoreId(anyLong()))
            .thenReturn(Optional.empty());
        Image businessImage = te.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.BUSINESS_INFO_IMAGE.getVariable(), "사업자등록증.png", false);
        Image storeImage = te.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.STORE_IMAGE.getVariable(), "매장사진.png", true);
        Store store = te.getStore(te.getMerchant(), account, te.getBankTypeKb(), te.getStoreStatusOpen(), businessImage, storeImage);

        when(storeRepository.getReferenceById(any(Long.class)))
            .thenReturn(te.persist(store));

        when(couponUsageStoreRepository.save(any(CouponUsageStore.class)))
            .thenAnswer(invocation -> te.persist(invocation.getArgument(0)));

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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "rate", 10);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumDiscountAmount", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumOrderPriceAndMaximumDiscountAmount(
            anyInt(), anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(new CouponTypePercent(
                invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

        Image businessImage = te.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.BUSINESS_INFO_IMAGE.getVariable(), "사업자등록증.png", false);
        Image storeImage = te.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.STORE_IMAGE.getVariable(), "매장사진.png", true);

        Store store = te.getStore(te.getMerchant(), account, te.getBankTypeKb(), te.getStoreStatusOpen(), businessImage, storeImage);

        when(couponUsageStoreRepository.findByStoreId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Store persistStore = te.persist(store, id);
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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageMerchantRepository.findByMerchantId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Merchant merchant = te.persist(te.getMerchant(), id);
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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageMerchantRepository.findByMerchantId(anyLong()))
            .thenReturn(Optional.empty());

        when(merchantRepository.getReferenceById(any(Long.class)))
            .thenReturn(te.persist(te.getMerchant()));

        when(couponUsageMerchantRepository.save(any(CouponUsageMerchant.class)))
            .thenAnswer(invocation -> te.persist(invocation.getArgument(0)));

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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "rate", 10);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumDiscountAmount", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumOrderPriceAndMaximumDiscountAmount(
            anyInt(), anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(new CouponTypePercent(
                invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

        when(couponUsageMerchantRepository.findByMerchantId(anyLong()))
            .thenAnswer(invocation -> {
                long id = invocation.getArgument(0);
                Merchant merchant = te.persist(te.getMerchant(), id);
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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(
                new CouponTypeCash(invocation.getArgument(0), invocation.getArgument(1))));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(te.persist(new CouponUsageAll())));

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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "rate", 10);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumDiscountAmount", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumOrderPriceAndMaximumDiscountAmount(
            anyInt(), anyInt(), anyInt()))
            .thenAnswer(invocation -> Optional.of(new CouponTypePercent(
                invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2))));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(te.persist(new CouponUsageAll())));

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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);

        when(couponTypeCashRepository.findByDiscountAmountAndMinimumOrderPrice(anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        when(couponTypeCashRepository.save(any(CouponTypeCash.class)))
            .thenAnswer(invocation -> te.persist(invocation.getArgument(0)));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(te.persist(new CouponUsageAll())));

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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "rate", 10);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumDiscountAmount", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumOrderPriceAndMaximumDiscountAmount(
            anyInt(), anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        when(couponTypePercentRepository.save(any(CouponTypePercent.class)))
            .thenAnswer(invocation -> te.persist(invocation.getArgument(0)));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.of(te.persist(new CouponUsageAll())));

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
        ReflectionTestUtils.setField(requestDto, "usagePeriod", 30);
        ReflectionTestUtils.setField(requestDto, "rate", 10);
        ReflectionTestUtils.setField(requestDto, "minimumOrderPrice", 10_000);
        ReflectionTestUtils.setField(requestDto, "maximumDiscountAmount", 30_000);

        when(couponTypePercentRepository.findByRateAndMinimumOrderPriceAndMaximumDiscountAmount(
            anyInt(), anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        when(couponTypePercentRepository.save(any(CouponTypePercent.class)))
            .thenAnswer(invocation -> te.persist(invocation.getArgument(0)));

        when(couponUsageAllRepository.findTopByOrderByIdAsc())
            .thenAnswer(invocation -> Optional.empty());

        assertThatThrownBy(() -> couponPolicyService.createAllPercentCouponPolicy(requestDto))
            .isInstanceOf(CouponUsageNotFoundException.class);
    }

    @Test
    @DisplayName("정책 삭제 테스트 - 정책 존재")
    void deletePolicyTest() throws Exception {
        CouponPolicy couponPolicy =
            te.persist(te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll()));
        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        assertThat(couponPolicy.isDeleted()).isFalse();

        couponPolicyService.deletePolicy(couponPolicy.getId());
        assertThat(couponPolicy.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("정책 삭제 테스트 - 정책 미존재")
    void deleteEmptyPolicyTest() throws Exception {
        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> couponPolicyService.deletePolicy(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("쿠폰 정책 목록 확인 테스트")
    void getProvableStoreCouponPolicies() throws Exception {
        List<SelectProvableStoreCouponPolicyResponseDto> selectProvableStoreCouponPolicyResponses =
            List.of(
                new SelectProvableStoreCouponPolicyResponseDto(
                    1L, te.getCouponTypeCash_1000_10000(), 1),
                new SelectProvableStoreCouponPolicyResponseDto(
                    2L, te.getCouponTypePercent_3_1000_10000(), 2)
            );

        when(couponPolicyRepository.lookupProvableStoreCouponPolicies(any(Long.class)))
            .thenReturn(selectProvableStoreCouponPolicyResponses);

        assertThat(couponPolicyService.getProvableStoreCouponPolicies(Long.MIN_VALUE))
            .hasSize(2);
    }
}
