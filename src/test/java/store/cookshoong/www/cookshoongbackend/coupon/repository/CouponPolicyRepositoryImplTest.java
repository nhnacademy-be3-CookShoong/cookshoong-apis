package store.cookshoong.www.cookshoongbackend.coupon.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.coupon.entity.*;
import store.cookshoong.www.cookshoongbackend.coupon.model.temp.SelectPolicyResponseTempDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
            "10000원 이상 주문 시 1000원 할인", LocalTime.of(1, 0, 0));
        storePercentCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageStore,
            "매장 퍼센트 쿠폰", "10000원 이상 주문 시 3%, 최대 1000원 할인",
            LocalTime.of(1, 0, 0));
        merchantCashCouponPolicy = new CouponPolicy(couponTypeCash, couponUsageMerchant,
            "가맹점 금액 쿠폰", "10000원 이상 주문 시 1000원 할인", LocalTime.of(1, 0, 0));
        merchantPercentCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageMerchant,
            "가맹점 퍼센트 쿠폰", "10000원 이상 주문 시 3%, 최대 1000원 할인",
            LocalTime.of(1, 0, 0));
        allCashCouponPolicy = new CouponPolicy(couponTypeCash, couponUsageAll, "전체 금액 쿠폰",
            "10000원 이상 주문 시 1000원 할인", LocalTime.of(1, 0, 0));
        allPercentCouponPolicy = new CouponPolicy(couponTypePercent, couponUsageAll, "전체 퍼센트 쿠폰",
            "10000원 이상 주문 시 3%, 최대 1000원 할인", LocalTime.of(1, 0, 0));

        em.persist(storeCashCouponPolicy);
        em.persist(storePercentCouponPolicy);
        em.persist(merchantCashCouponPolicy);
        em.persist(merchantPercentCouponPolicy);
        em.persist(allCashCouponPolicy);
        em.persist(allPercentCouponPolicy);
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
            assertThat(temp.getExpirationTime()).isEqualTo(policy.getExpirationTime());
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
            assertThat(temp.getExpirationTime()).isEqualTo(policy.getExpirationTime());
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
            assertThat(temp.getExpirationTime()).isEqualTo(policy.getExpirationTime());
        }
    }
}
