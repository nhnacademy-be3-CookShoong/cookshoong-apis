package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
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
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.SelectPolicyResponseTempDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class})
class CouponPolicyRepositoryImplTest {
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
        Page<SelectPolicyResponseTempDto> selectPolicyResponseTemps =
            couponPolicyRepository.lookupStorePolicy(store.getId(), Pageable.ofSize(10));
        List<CouponPolicy> storePolicies = List.of(storeCashCouponPolicy, storePercentCouponPolicy);

        assertThat(selectPolicyResponseTemps.getTotalElements()).isEqualTo(storePolicies.size());

        Iterator<SelectPolicyResponseTempDto> tempIterator = selectPolicyResponseTemps.iterator();
        Iterator<CouponPolicy> policyIterator = storePolicies.iterator();

        while (tempIterator.hasNext()) {
            SelectPolicyResponseTempDto temp = tempIterator.next();
            CouponPolicy policy = policyIterator.next();

            assertThat(temp.getId()).isEqualTo(policy.getId());
            assertThat(temp.getCouponType().getId()).isEqualTo(policy.getCouponType().getId());
            assertThat(temp.getName()).isEqualTo(policy.getName());
            assertThat(temp.getDescription()).isEqualTo(policy.getDescription());
            assertThat(temp.getUsagePeriod()).isEqualTo(policy.getUsagePeriod());
        }
    }

    @Test
    @DisplayName("가맹점 정책 확인")
    void lookupMerchantPolicyTest() throws Exception {
        Page<SelectPolicyResponseTempDto> selectPolicyResponseTemps =
            couponPolicyRepository.lookupMerchantPolicy(merchant.getId(), Pageable.ofSize(10));
        List<CouponPolicy> merchantPolicies = List.of(merchantCashCouponPolicy, merchantPercentCouponPolicy);

        assertThat(selectPolicyResponseTemps.getTotalElements()).isEqualTo(merchantPolicies.size());

        Iterator<SelectPolicyResponseTempDto> tempIterator = selectPolicyResponseTemps.iterator();
        Iterator<CouponPolicy> policyIterator = merchantPolicies.iterator();

        while (tempIterator.hasNext()) {
            SelectPolicyResponseTempDto temp = tempIterator.next();
            CouponPolicy policy = policyIterator.next();

            assertThat(temp.getId()).isEqualTo(policy.getId());
            assertThat(temp.getCouponType().getId()).isEqualTo(policy.getCouponType().getId());
            assertThat(temp.getName()).isEqualTo(policy.getName());
            assertThat(temp.getDescription()).isEqualTo(policy.getDescription());
            assertThat(temp.getUsagePeriod()).isEqualTo(policy.getUsagePeriod());
        }
    }

    @Test
    @DisplayName("사용처 전체 정책 확인")
    void lookupAllPolicyTest() throws Exception {
        Page<SelectPolicyResponseTempDto> selectPolicyResponseTemps =
            couponPolicyRepository.lookupAllPolicy(Pageable.ofSize(10));
        List<CouponPolicy> allPolicies = List.of(allCashCouponPolicy, allPercentCouponPolicy);

        assertThat(selectPolicyResponseTemps.getTotalElements()).isEqualTo(allPolicies.size());

        Iterator<SelectPolicyResponseTempDto> tempIterator = selectPolicyResponseTemps.iterator();
        Iterator<CouponPolicy> policyIterator = allPolicies.iterator();

        while (tempIterator.hasNext()) {
            SelectPolicyResponseTempDto temp = tempIterator.next();
            CouponPolicy policy = policyIterator.next();

            assertThat(temp.getId()).isEqualTo(policy.getId());
            assertThat(temp.getCouponType().getId()).isEqualTo(policy.getCouponType().getId());
            assertThat(temp.getName()).isEqualTo(policy.getName());
            assertThat(temp.getDescription()).isEqualTo(policy.getDescription());
            assertThat(temp.getUsagePeriod()).isEqualTo(policy.getUsagePeriod());
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

        Page<SelectPolicyResponseTempDto> selectPolicyResponseTemps =
            couponPolicyRepository.lookupStorePolicy(store.getId(), Pageable.ofSize(10));

        assertThat(selectPolicyResponseTemps).hasSize(2);

        SelectPolicyResponseTempDto firstPolicyResponse = selectPolicyResponseTemps.getContent().get(0);
        assertThat(firstPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideCount);
        assertThat(firstPolicyResponse.getIssueCouponCount()).isEqualTo(provideCount + provideToCustomerCount);

        SelectPolicyResponseTempDto secondPolicyResponse = selectPolicyResponseTemps.getContent().get(1);
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

        Page<SelectPolicyResponseTempDto> selectPolicyResponseTemps =
            couponPolicyRepository.lookupMerchantPolicy(merchant.getId(), Pageable.ofSize(10));

        assertThat(selectPolicyResponseTemps).hasSize(2);

        SelectPolicyResponseTempDto firstPolicyResponse = selectPolicyResponseTemps.getContent().get(0);
        assertThat(firstPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideCount);
        assertThat(firstPolicyResponse.getIssueCouponCount()).isEqualTo(provideCount + provideToCustomerCount);

        SelectPolicyResponseTempDto secondPolicyResponse = selectPolicyResponseTemps.getContent().get(1);
        assertThat(secondPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideSecondCount);
        assertThat(secondPolicyResponse.getIssueCouponCount())
            .isEqualTo(provideSecondCount + provideToCustomerSecondCount);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    @DisplayName("전체 쿠폰 발행 횟수 확인")
    void issueAllCouponCountTest(int provideCount, int provideToCustomerCount,
                                 int provideSecondCount, int provideToCustomerSecondCount) throws Exception {
        provide(allCashCouponPolicy, provideCount);
        provideToCustomer(allCashCouponPolicy, provideToCustomerCount);
        provide(allPercentCouponPolicy, provideSecondCount);
        provideToCustomer(allPercentCouponPolicy, provideToCustomerSecondCount);

        Page<SelectPolicyResponseTempDto> selectPolicyResponseTemps =
            couponPolicyRepository.lookupAllPolicy(Pageable.ofSize(10));

        assertThat(selectPolicyResponseTemps).hasSize(2);

        SelectPolicyResponseTempDto firstPolicyResponse = selectPolicyResponseTemps.getContent().get(0);
        assertThat(firstPolicyResponse.getUnclaimedCouponCount()).isEqualTo(provideCount);
        assertThat(firstPolicyResponse.getIssueCouponCount()).isEqualTo(provideCount + provideToCustomerCount);

        SelectPolicyResponseTempDto secondPolicyResponse = selectPolicyResponseTemps.getContent().get(1);
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
            em.persist(new IssueCoupon(couponPolicy)).provideToUser(customer);
        }
    }
}
