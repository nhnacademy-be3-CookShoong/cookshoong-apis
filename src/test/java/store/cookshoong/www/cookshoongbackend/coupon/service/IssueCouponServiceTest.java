package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.OptionalInt;
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
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
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

    CreateIssueCouponRequestDto requestDto;

    @BeforeEach
    void beforeEach() {
        requestDto = te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "issueQuantity", 1L);
        ReflectionTestUtils.setField(requestDto, "couponPolicyId", Long.MIN_VALUE);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 1000})
    @DisplayName("매장 쿠폰 발행 성공")
    void createStoreIssueCouponSuccessTest(int issueQuantity) throws Exception {

        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageStore(tpe.getOpenStore())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(requestDto, "issueQuantity", (long) issueQuantity);

        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        when(issueCouponRepository.save(any()))
            .thenReturn(issueCoupon);

        issueCouponService.createIssueCoupon(requestDto);

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

        ReflectionTestUtils.setField(requestDto, "issueQuantity", (long) issueQuantity);

        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        when(issueCouponRepository.save(any()))
            .thenReturn(issueCoupon);

        issueCouponService.createIssueCoupon(requestDto);

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

        ReflectionTestUtils.setField(requestDto, "issueQuantity", (long) issueQuantity);

        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        when(issueCouponRepository.save(any()))
            .thenReturn(issueCoupon);

        issueCouponService.createIssueCoupon(requestDto);

        verify(issueCouponRepository, times(issueQuantity)).save(any());
    }

    @Test
    @DisplayName("쿠폰 발행 실패 - 쿠폰 정책 미존재")
    void createIssueCouponNonCouponPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> issueCouponService.createIssueCoupon(requestDto));
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
            () -> issueCouponService.createIssueCoupon(requestDto));
    }
}
