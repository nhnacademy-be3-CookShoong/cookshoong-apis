package store.cookshoong.www.cookshoongbackend.coupon.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.NotAllowedIssueMethodException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponPolicyRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.CouponRedisRepository;
import store.cookshoong.www.cookshoongbackend.coupon.repository.IssueCouponRepository;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;
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
    CouponRedisRepository couponRedisRepository;

    CreateIssueCouponRequestDto createIssueCouponRequestDto;

    @SpyBean
    RedisTemplate<String, Object> couponRedisTemplate;

    @BeforeEach
    void beforeEach() {
        createIssueCouponRequestDto = te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", 1L);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "couponPolicyId", Long.MIN_VALUE);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.NORMAL);
    }

    @Test
    @DisplayName("쿠폰 발행 실패 - 쿠폰 정책 미존재")
    void createIssueCouponNonCouponPolicyFailTest() throws Exception {
        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        assertThrowsExactly(CouponPolicyNotFoundException.class,
            () -> issueCouponService.createIssueCoupon(createIssueCouponRequestDto));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("couponUsageAndIssueMethod")
    @DisplayName("쿠폰 발행 실패 - 쿠폰 사용처 발행방법 미일치")
    void createIssueCouponUsageMismatchMethodFailTest(String displayName, Class<CouponUsage> clazz,
                                                      IssueMethod issueMethod) throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.createUsingDeclared(clazz)));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", issueMethod);

        assertThrowsExactly(NotAllowedIssueMethodException.class,
            () -> issueCouponService.createIssueCoupon(createIssueCouponRequestDto));
    }

    public static Stream<Arguments> couponUsageAndIssueMethod() throws Throwable {

        return Stream.of(
            Arguments.of("전체 - 일반발행", CouponUsageAll.class, IssueMethod.NORMAL),
            Arguments.of("가맹점 - 일반발행", CouponUsageMerchant.class, IssueMethod.NORMAL),
            Arguments.of("매장 - 전체발행", CouponUsageStore.class, IssueMethod.BULK),
            Arguments.of("매장 - 이벤트발행", CouponUsageStore.class, IssueMethod.EVENT)
        );
    }

    @Test
    @DisplayName("쿠폰 발행 실패 - 유효 쿠폰 개수 초과")
    void createIssueCouponOverLimitFailTest() throws Exception {
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.createUsingDeclared(CouponUsageStore.class)));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", Long.MAX_VALUE);

        assertThrowsExactly(IssueCouponOverCountException.class,
            () -> issueCouponService.createIssueCoupon(createIssueCouponRequestDto));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 1000})
    @DisplayName("매장 쿠폰 발행 성공")
    void createStoreIssueCouponSuccessTest(int issueQuantity) throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.NORMAL);

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
    @DisplayName("가맹점 이벤트 쿠폰 발행 성공")
    void createMerchantIssueCouponSuccessTest(int issueQuantity) throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.EVENT);
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageMerchant(te.getMerchant())));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueQuantity);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        verify(issueCouponRepository).saveAllAndFlush(any());
        verify(couponRedisRepository).bulkInsertCouponCode(any(), anyString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9, 1000})
    @DisplayName("전체 이벤트 쿠폰 발행 성공")
    void createUsageAllIssueCouponSuccessTest(int issueQuantity) throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.EVENT);
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll()));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", (long) issueQuantity);

        issueCouponService.createIssueCoupon(createIssueCouponRequestDto);

        verify(issueCouponRepository).saveAllAndFlush(any());
        verify(couponRedisRepository).bulkInsertCouponCode(any(), anyString());
    }

    @Test
    @DisplayName("전체 이벤트 쿠폰 발행 실패 - 허용되지 않은 발행 방법")
    void createIssueCouponThrowNotAllowedMethodExceptionFailTest() throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueMethod", IssueMethod.BULK);
        CouponPolicy couponPolicy = te.persist(
            te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll()));

        when(couponPolicyRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(couponPolicy));

        assertThrowsExactly(NotAllowedIssueMethodException.class, () ->
            issueCouponService.createIssueCoupon(createIssueCouponRequestDto));
    }
}
