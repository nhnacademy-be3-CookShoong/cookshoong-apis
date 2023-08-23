package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableCouponPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeCashVo;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypePercentVo;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class})
class CouponPolicyRepositoryImplTest {
    static int DAY_OFFSET;

    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity te;

    @Autowired
    TestPersistEntity tpe;

    @Autowired
    CouponPolicyRepository couponPolicyRepository;

    Store store;
    Merchant merchant;
    CouponPolicy storeCashCouponPolicy;
    CouponPolicy storePercentCouponPolicy;
    CouponPolicy merchantCashCouponPolicy;
    CouponPolicy merchantPercentCouponPolicy;
    CouponPolicy allCashCouponPolicy;
    CouponPolicy allPercentCouponPolicy;
    Account customer;

    @BeforeAll
    static void beforeAll() throws NoSuchFieldException, IllegalAccessException {
        Field dayOffset = CouponPolicy.class.getDeclaredField("DAY_OFFSET");
        dayOffset.setAccessible(true);
        DAY_OFFSET = dayOffset.getInt(null);
    }

    @BeforeEach
    void beforeEach() {
        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();
        CouponTypePercent couponTypePercent = te.getCouponTypePercent_3_1000_10000();
        store = tpe.getOpenStore();
        CouponUsageStore couponUsageStore = te.getCouponUsageStore(store);
        Store openMerchantStore = tpe.getOpenMerchantStore();
        merchant = openMerchantStore.getMerchant();
        CouponUsageMerchant couponUsageMerchant = em.persist(new CouponUsageMerchant(merchant));
        CouponUsageAll couponUsageAll = em.persist(new CouponUsageAll());

        storeCashCouponPolicy = new CouponPolicy(couponTypeCash, couponUsageStore, "매장 금액 쿠폰",
            "10000원 이상 주문 시 1000원 할인", 30);
        storePercentCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageStore,
            "매장 퍼센트 쿠폰", "10000원 이상 주문 시 3%, 최대 1000원 할인",
            30);
        merchantCashCouponPolicy = new CouponPolicy(couponTypeCash, couponUsageMerchant,
            "가맹점 금액 쿠폰", "10000원 이상 주문 시 1000원 할인", 30);
        merchantPercentCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageMerchant,
            "가맹점 퍼센트 쿠폰", "10000원 이상 주문 시 3%, 최대 1000원 할인",
            30);
        allCashCouponPolicy = new CouponPolicy(couponTypeCash, couponUsageAll, "전체 금액 쿠폰",
            "10000원 이상 주문 시 1000원 할인", 30);
        allPercentCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageAll, "전체 퍼센트 쿠폰",
            "10000원 이상 주문 시 3%, 최대 1000원 할인", 30);

        for (int i = 0; i < 30; i++) {
            CouponPolicy deleteCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageAll, "전체 퍼센트 쿠폰",
                "10000원 이상 주문 시 3%, 최대 1000원 할인", 30);
            deleteCouponPolicy.delete();
            em.persist(deleteCouponPolicy);
        }

        em.persist(storeCashCouponPolicy);
        em.persist(storePercentCouponPolicy);
        em.persist(merchantCashCouponPolicy);
        em.persist(merchantPercentCouponPolicy);
        em.persist(allCashCouponPolicy);
        em.persist(allPercentCouponPolicy);

        customer = tpe.getLevelOneActiveCustomer();

        em.flush();
        em.clear();

        System.out.println("=======================================test ready=======================================");
        System.out.println("=======================================test ready=======================================");
        System.out.println("=======================================test ready=======================================");
        System.out.println("=======================================test ready=======================================");
        System.out.println("=======================================test ready=======================================");
        System.out.println("=======================================test ready=======================================");
        System.out.println("=======================================test ready=======================================");
    }

    @Test
    @DisplayName("매장 정책 확인")
    void lookupStorePolicyTest() throws Exception {
        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyRepository.lookupStorePolicy(store.getId(), Pageable.ofSize(10));
        List<CouponPolicy> storePolicies = List.of(storeCashCouponPolicy, storePercentCouponPolicy);

        assertThat(selectPolicyResponses.getTotalElements()).isEqualTo(storePolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<CouponPolicy> policyIterator = storePolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            CouponPolicy policy = policyIterator.next();

            assertThat(response.getId()).isEqualTo(policy.getId());
            assertThat(response.getCouponTypeResponse()).isInstanceOfAny(
                CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(policy.getName());
            assertThat(response.getDescription()).isEqualTo(policy.getDescription());
            assertThat(response.getUsagePeriod()).isEqualTo(policy.getUsagePeriod() + DAY_OFFSET);
        }
    }

    @Test
    @DisplayName("가맹점 정책 확인")
    void lookupMerchantPolicyTest() throws Exception {
        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyRepository.lookupMerchantPolicy(merchant.getId(), Pageable.ofSize(10));
        List<CouponPolicy> merchantPolicies = List.of(merchantCashCouponPolicy, merchantPercentCouponPolicy);

        assertThat(selectPolicyResponses.getTotalElements()).isEqualTo(merchantPolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<CouponPolicy> policyIterator = merchantPolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            CouponPolicy policy = policyIterator.next();

            assertThat(response.getId()).isEqualTo(policy.getId());
            assertThat(response.getCouponTypeResponse()).isInstanceOfAny(
                CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(policy.getName());
            assertThat(response.getDescription()).isEqualTo(policy.getDescription());
            assertThat(response.getUsagePeriod()).isEqualTo(policy.getUsagePeriod() + DAY_OFFSET);
        }
    }

    @Test
    @DisplayName("사용처 전체 정책 확인")
    void lookupAllPolicyTest() throws Exception {
        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyRepository.lookupAllPolicy(Pageable.ofSize(10));
        List<CouponPolicy> allPolicies = List.of(allCashCouponPolicy, allPercentCouponPolicy);

        assertThat(selectPolicyResponses.getTotalElements()).isEqualTo(allPolicies.size());

        Iterator<SelectPolicyResponseDto> responseIterator = selectPolicyResponses.iterator();
        Iterator<CouponPolicy> policyIterator = allPolicies.iterator();

        while (responseIterator.hasNext()) {
            SelectPolicyResponseDto response = responseIterator.next();
            CouponPolicy policy = policyIterator.next();

            assertThat(response.getId()).isEqualTo(policy.getId());
            assertThat(response.getCouponTypeResponse()).isInstanceOfAny(
                CouponTypeCashVo.class, CouponTypePercentVo.class);
            assertThat(response.getName()).isEqualTo(policy.getName());
            assertThat(response.getDescription()).isEqualTo(policy.getDescription());
            assertThat(response.getUsagePeriod()).isEqualTo(policy.getUsagePeriod() + DAY_OFFSET);
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    @DisplayName("매장 쿠폰 발행 횟수 확인")
    void issueStoreCouponCountTest(int provideCount, int provideToCustomerCount,
                                   int provideSecondCount, int provideToCustomerSecondCount) throws Exception {
        provide(storeCashCouponPolicy, provideCount);
        provideToCustomer(storeCashCouponPolicy, provideToCustomerCount);
        provide(storePercentCouponPolicy, provideSecondCount);
        provideToCustomer(storePercentCouponPolicy, provideToCustomerSecondCount);

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyRepository.lookupStorePolicy(store.getId(), Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(2);

        SelectPolicyResponseDto firstPolicyResponse = selectPolicyResponses.getContent().get(0);
        assertThat(firstPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideCount);
        assertThat(firstPolicyResponse.getIssueCouponCount()).isEqualTo(provideCount + provideToCustomerCount);

        SelectPolicyResponseDto secondPolicyResponse = selectPolicyResponses.getContent().get(1);
        assertThat(secondPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideSecondCount);
        assertThat(secondPolicyResponse.getIssueCouponCount())
            .isEqualTo(provideSecondCount + provideToCustomerSecondCount);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    @DisplayName("가맹점 쿠폰 발행 횟수 확인")
    void issueMerchantCouponCountTest(int provideCount, int provideToCustomerCount,
                                      int provideSecondCount, int provideToCustomerSecondCount) throws Exception {
        provide(merchantCashCouponPolicy, provideCount);
        provideToCustomer(merchantCashCouponPolicy, provideToCustomerCount);
        provide(merchantPercentCouponPolicy, provideSecondCount);
        provideToCustomer(merchantPercentCouponPolicy, provideToCustomerSecondCount);

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyRepository.lookupMerchantPolicy(merchant.getId(), Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(2);

        SelectPolicyResponseDto firstPolicyResponse = selectPolicyResponses.getContent().get(0);
        assertThat(firstPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideCount);
        assertThat(firstPolicyResponse.getIssueCouponCount()).isEqualTo(provideCount + provideToCustomerCount);

        SelectPolicyResponseDto secondPolicyResponse = selectPolicyResponses.getContent().get(1);
        assertThat(secondPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideSecondCount);
        assertThat(secondPolicyResponse.getIssueCouponCount())
            .isEqualTo(provideSecondCount + provideToCustomerSecondCount);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    @DisplayName("전체 쿠폰 발행 횟수 확인 - 삭제된 정책 카운트 무시")
    void issueAllCouponCountTest(int provideCount, int provideToCustomerCount,
                                 int provideSecondCount, int provideToCustomerSecondCount) throws Exception {
        provide(allCashCouponPolicy, provideCount);
        provideToCustomer(allCashCouponPolicy, provideToCustomerCount);
        provide(allPercentCouponPolicy, provideSecondCount);
        provideToCustomer(allPercentCouponPolicy, provideToCustomerSecondCount);

        Page<SelectPolicyResponseDto> selectPolicyResponses =
            couponPolicyRepository.lookupAllPolicy(Pageable.ofSize(10));

        assertThat(selectPolicyResponses).hasSize(2);
        assertThat(selectPolicyResponses.getTotalElements()).isEqualTo(2);
        assertThat(selectPolicyResponses.getTotalPages())
            .isEqualTo(1 + selectPolicyResponses.getTotalElements() / 10);

        SelectPolicyResponseDto firstPolicyResponse = selectPolicyResponses.getContent().get(0);
        assertThat(firstPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideCount);
        assertThat(firstPolicyResponse.getIssueCouponCount()).isEqualTo(provideCount + provideToCustomerCount);

        SelectPolicyResponseDto secondPolicyResponse = selectPolicyResponses.getContent().get(1);
        assertThat(secondPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideSecondCount);
        assertThat(secondPolicyResponse.getIssueCouponCount())
            .isEqualTo(provideSecondCount + provideToCustomerSecondCount);
    }


    public static Stream<Arguments> parameters() throws Throwable {

        return Stream.of(
            Arguments.of(1, 2, 3, 4),
            Arguments.of(3, 1, 4, 1),
            Arguments.of(5, 9, 2, 6),
            Arguments.of(5, 3, 5, 8),
            Arguments.of(9, 7, 9, 3),
            Arguments.of(2, 3, 8, 4),
            Arguments.of(6, 2, 6, 4),
            Arguments.of(3, 3, 8, 3)
        );
    }

    void provide(CouponPolicy couponPolicy, int count) {
        for (int i = 0; i < count; i++) {
            em.persist(new IssueCoupon(couponPolicy));
        }
    }

    void provideToCustomer(CouponPolicy couponPolicy, int count) {
        for (int i = 0; i < count; i++) {
            em.persist(new IssueCoupon(couponPolicy)).provideToAccount(customer);
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    @DisplayName("수령 가능한 쿠폰 개수 확인")
    void lookupUnclaimedCouponCountTest(int provideCount, int provideToCustomerCount,
                                        int provideSecondCount, int provideToCustomerSecondCount) throws Exception {
        provide(allCashCouponPolicy, provideCount);
        provideToCustomer(allCashCouponPolicy, provideToCustomerCount);
        provide(allPercentCouponPolicy, provideSecondCount);
        provideToCustomer(allPercentCouponPolicy, provideToCustomerSecondCount);

        Long unclaimedAllCashCouponCount =
            couponPolicyRepository.lookupUnclaimedCouponCount(allCashCouponPolicy.getId());
        assertThat(unclaimedAllCashCouponCount).isEqualTo(provideCount);

        Long unclaimedAllPercentCouponCount =
            couponPolicyRepository.lookupUnclaimedCouponCount(allPercentCouponPolicy.getId());
        assertThat(unclaimedAllPercentCouponCount).isEqualTo(provideSecondCount);
    }

    @Test
    @DisplayName("제공 가능한 쿠폰 정책 확인 - 없음")
    void lookupProvableStoreCouponPoliciesEmptyTest() throws Exception {
        List<SelectProvableCouponPolicyResponseDto> selectProvableStoreCouponPolicyResponses =
            couponPolicyRepository.lookupProvableStoreCouponPolicies(store.getId());

        assertThat(selectProvableStoreCouponPolicyResponses).isEmpty();
    }
    @Test
    @DisplayName("제공 가능한 쿠폰 정책 확인 - 삭제됨")
    void lookupProvableStoreCouponPoliciesDeletedTest() throws Exception {
        em.persist(new IssueCoupon(storeCashCouponPolicy));
        em.persist(new IssueCoupon(storePercentCouponPolicy));

        couponPolicyRepository.findById(storeCashCouponPolicy.getId())
            .orElseThrow(CouponPolicyNotFoundException::new)
            .delete();

        couponPolicyRepository.findById(storePercentCouponPolicy.getId())
            .orElseThrow(CouponPolicyNotFoundException::new)
            .delete();

        List<SelectProvableCouponPolicyResponseDto> selectProvableStoreCouponPolicyResponses =
            couponPolicyRepository.lookupProvableStoreCouponPolicies(store.getId());

        assertThat(selectProvableStoreCouponPolicyResponses).isEmpty();
    }

    @Test
    @DisplayName("제공 가능한 쿠폰 정책 확인 - 숨겨짐")
    void lookupProvableStoreCouponPoliciesHiddenTest() throws Exception {
        em.persist(new IssueCoupon(allCashCouponPolicy));
        em.persist(new IssueCoupon(allPercentCouponPolicy));

        couponPolicyRepository.findById(allCashCouponPolicy.getId())
            .orElseThrow(CouponPolicyNotFoundException::new)
            .hide();

        couponPolicyRepository.findById(allPercentCouponPolicy.getId())
            .orElseThrow(CouponPolicyNotFoundException::new)
            .hide();

        List<SelectProvableCouponPolicyResponseDto> selectProvableUsageAllCouponPolicyResponses =
            couponPolicyRepository.lookupProvableUsageAllCouponPolicies();

        assertThat(selectProvableUsageAllCouponPolicyResponses).isEmpty();
    }

    @Test
    @DisplayName("제공 가능한 매장 쿠폰 정책 확인")
    void lookupProvableStoreCouponPoliciesTest() throws Exception {
        em.persist(new IssueCoupon(storeCashCouponPolicy));
        em.persist(new IssueCoupon(storePercentCouponPolicy));

        List<SelectProvableCouponPolicyResponseDto> selectProvableStoreCouponPolicyResponses =
            couponPolicyRepository.lookupProvableStoreCouponPolicies(store.getId());

        assertThat(selectProvableStoreCouponPolicyResponses).hasSize(2);
    }

    @Test
    @DisplayName("제공 가능한 가맹점 쿠폰 정책 확인")
    void lookupProvableMerchantCouponPoliciesTest() throws Exception {
        em.persist(new IssueCoupon(merchantCashCouponPolicy));
        em.persist(new IssueCoupon(merchantPercentCouponPolicy));

        List<SelectProvableCouponPolicyResponseDto> selectProvableMerchantCouponPolicyResponses =
            couponPolicyRepository.lookupProvableMerchantCouponPolicies(merchant.getId());

        assertThat(selectProvableMerchantCouponPolicyResponses).hasSize(2);
    }

    @Test
    @DisplayName("제공 가능한 모든 사용처 쿠폰 정책 확인")
    void lookupProvableUsageAllCouponPoliciesTest() throws Exception {
        em.persist(new IssueCoupon(allCashCouponPolicy));
        em.persist(new IssueCoupon(allPercentCouponPolicy));

        List<SelectProvableCouponPolicyResponseDto> selectProvableUsageAllCouponPolicyResponses =
            couponPolicyRepository.lookupProvableUsageAllCouponPolicies();

        assertThat(selectProvableUsageAllCouponPolicyResponses).hasSize(2);
    }

    @Test
    @DisplayName("매장 이벤트 유효한지 확인 - 수령 가능한 쿠폰 없음")
    void isOfferCouponInStoreFalseTest() throws Exception {
        assertThat(couponPolicyRepository.isOfferCouponInStore(store.getId()))
            .isFalse();
    }

    @Test
    @DisplayName("매장 이벤트 유효한지 확인 - 수령 가능한 쿠폰 있음")
    void isOfferCouponInStoreTest() throws Exception {
        te.getIssueCoupon(storeCashCouponPolicy);

        assertThat(couponPolicyRepository.isOfferCouponInStore(store.getId()))
            .isTrue();
    }

    @Test
    @DisplayName("매장 이벤트 유효한지 확인 - 유효 쿠폰 유저가 받아감")
    void isOfferCouponInStoreAlreadyProvideAccountTest() throws Exception {
        IssueCoupon issueCoupon = te.getIssueCoupon(storeCashCouponPolicy);
        issueCoupon.provideToAccount(customer);

        assertThat(couponPolicyRepository.isOfferCouponInStore(store.getId()))
            .isFalse();
    }

    @Test
    @DisplayName("매장 이벤트 유효한지 확인 - 정책 삭제됨")
    void isOfferCouponInStoreDeletedTest() throws Exception {
        te.getIssueCoupon(storeCashCouponPolicy);
        storeCashCouponPolicy.delete();
        em.merge(storeCashCouponPolicy);

        assertThat(couponPolicyRepository.isOfferCouponInStore(store.getId()))
            .isFalse();
    }

    @Test
    @DisplayName("매장 이벤트 유효한지 확인 - 정책 숨겨짐")
    void isOfferCouponInStoreHiddenTest() throws Exception {
        te.getIssueCoupon(storeCashCouponPolicy);
        storeCashCouponPolicy.hide();
        em.merge(storeCashCouponPolicy);

        assertThat(couponPolicyRepository.isOfferCouponInStore(store.getId()))
            .isFalse();
    }
}

