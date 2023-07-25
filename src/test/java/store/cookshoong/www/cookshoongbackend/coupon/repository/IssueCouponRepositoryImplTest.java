package store.cookshoong.www.cookshoongbackend.coupon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import({TestPersistEntity.class, QueryDslConfig.class})
class IssueCouponRepositoryImplTest {
    static final String USE_DESCRIPTION = "사용했어요!";
    static final String CANCEL_DESCRIPTION = "다시 사용하셔도 좋아요!";

    static Map<Boolean, List<String>> descriptionCheck = Map.of(
        true, new ArrayList<>() {{
            add(CANCEL_DESCRIPTION);
            add(null);
        }},
        false, new ArrayList<>() {{
            add(USE_DESCRIPTION);
            add(null);
        }}
    );

    static List<String> all = new ArrayList<>() {{
        add(USE_DESCRIPTION);
        add(CANCEL_DESCRIPTION);
        add(null);
    }};

    @Autowired
    IssueCouponRepository issueCouponRepository;

    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity te;

    @Autowired
    TestPersistEntity tpe;

    Account customer;
    Store hasAllUsageCouponMerchant;
    Store hasMerchantUsageCouponMerchant;
    Store hasStoreUsageCoupon;
    Store hasNoCoupon;

    @BeforeEach
    void beforeEach() {
        customer = tpe.getLevelOneActiveCustomer();
        Merchant merchant = te.getMerchant();
        Image businessImage = te.getImage("사업자등록증.jpg", false);
        Image storeImage = te.getImage("우리 매장 대표사진.jpg", true);
        hasAllUsageCouponMerchant =
            te.getStore(merchant, tpe.getLevelOneActiveCustomer(), te.getBankTypeKb(), te.getStoreStatusOpen(), businessImage, storeImage);
        hasMerchantUsageCouponMerchant =
            te.getStore(merchant, tpe.getLevelOneActiveCustomer(), te.getBankTypeKb(), te.getStoreStatusOpen(), businessImage, storeImage);
        hasStoreUsageCoupon = tpe.getOpenStore();
        hasNoCoupon = tpe.getOpenStore();

        CouponTypeCash couponTypeCash = te.getCouponTypeCash_1000_10000();

        // 가맹점 매장
        CouponUsageMerchant couponUsageMerchant = te.getCouponUsageMerchant(merchant);
        CouponUsageStore couponAllUsageStore = te.getCouponUsageStore(hasAllUsageCouponMerchant);
        CouponUsageStore couponStoreUsageStore = te.getCouponUsageStore(hasStoreUsageCoupon);
        CouponUsageAll couponUsageAll = te.getCouponUsageAll();

        CouponPolicy couponPolicyExpired = em.persist(
            new CouponPolicy(couponTypeCash, couponUsageAll, "만료된 쿠폰", "", 0));
        CouponPolicy couponPolicyMerchant = te.getCouponPolicy(couponTypeCash, couponUsageMerchant);
        CouponPolicy couponPolicyAllUsageStore = te.getCouponPolicy(couponTypeCash, couponAllUsageStore);
        CouponPolicy couponPolicyStoreUsageStore = te.getCouponPolicy(couponTypeCash, couponStoreUsageStore);
        CouponPolicy couponPolicyAll = te.getCouponPolicy(couponTypeCash, couponUsageAll);

        List<IssueCoupon> issueCoupons = new ArrayList<>();
        IssueCoupon expiredIssueCoupon = te.getIssueCoupon(couponPolicyExpired);
        issueCoupons.add(expiredIssueCoupon);
        issueCoupons.add(te.getIssueCoupon(couponPolicyMerchant));
        issueCoupons.add(te.getIssueCoupon(couponPolicyAllUsageStore));
        issueCoupons.add(te.getIssueCoupon(couponPolicyStoreUsageStore));
        issueCoupons.add(te.getIssueCoupon(couponPolicyAll));

        issueCoupons.add(te.getIssueCoupon(couponPolicyMerchant));
        issueCoupons.add(te.getIssueCoupon(couponPolicyAllUsageStore));
        issueCoupons.add(te.getIssueCoupon(couponPolicyStoreUsageStore));
        issueCoupons.add(te.getIssueCoupon(couponPolicyAll));

        issueCoupons.add(te.getIssueCoupon(couponPolicyMerchant));
        issueCoupons.add(te.getIssueCoupon(couponPolicyAllUsageStore));
        issueCoupons.add(te.getIssueCoupon(couponPolicyStoreUsageStore));
        issueCoupons.add(te.getIssueCoupon(couponPolicyAll));

        issueCoupons.add(te.getIssueCoupon(couponPolicyStoreUsageStore));

        issueCoupons.forEach(issueCoupon -> issueCoupon.provideToAccount(customer));

        ReflectionTestUtils.setField(expiredIssueCoupon, "expirationDate",
            LocalDate.of(1000, 1, 1));

        Order order = tpe.createTestOrder();

        issueCoupons.stream()
            .skip(5)
            .forEach(issueCoupon -> te.getCouponLog(
                issueCoupon, te.getCouponLogType("USE", USE_DESCRIPTION), order));

        issueCoupons.stream()
            .skip(11)
            .forEach(issueCoupon -> te.getCouponLog(
                issueCoupon, te.getCouponLogType("CANCEL", CANCEL_DESCRIPTION), order));

        em.flush();
        em.clear();
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
        System.out.println("=======================================");
    }

    List<String> selectDescriptionCheck(Boolean usable) {
        if (usable == null) {
            return all;
        }

        return descriptionCheck.get(usable);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("parameters")
    @DisplayName("소유 쿠폰 목록 확인 테스트")
    void lookupAllOwnCouponsAllTest(String displayName, Boolean usable, String store, int size) throws Exception {
        Map<String, Long> searchStoreId = Map.of(
            "가맹점매장", hasAllUsageCouponMerchant.getId(),
            "가맹점", hasMerchantUsageCouponMerchant.getId(),
            "매장", hasStoreUsageCoupon.getId(),
            "쿠폰없음", hasNoCoupon.getId()
        );

        Page<SelectOwnCouponResponseDto> couponResponses =
            issueCouponRepository.lookupAllOwnCoupons(
                customer.getId(), Pageable.ofSize(20), usable,
                searchStoreId.getOrDefault(store, null));

        assertThat(couponResponses).hasSize(size);
        assertThat(couponResponses.getTotalElements()).isEqualTo(size);
        assertThat(couponResponses.getTotalPages()).isEqualTo(1 + size / 20);
        couponResponses.forEach(
            couponResponseDto -> assertThat(selectDescriptionCheck(usable))
                .contains(couponResponseDto.getLogTypeDescription())
        );
    }

    // 전체 쿠폰 14개
    // 사용한 쿠폰 9개
    // 사용했다가 주문 취소되어 다시 사용 가능한 쿠폰 3개
    // 만료된 쿠폰 1개
    // 사용 가능한 쿠폰 4 + 3 = 7개
    // 사용 불가능한 쿠폰 10 - 3 = 7개
    // 네네치킨 쿠폰 3장 중 2장 사용
    // 네네치킨 매장 쿠폰 3장 중 2장 사용
    // 개인 매장 쿠폰 4장 중 3장 사용, 2장 취소
    // 모든 매장 쿠폰 4장 중 2장 사용, 1장 취소, 1장 만료
    public static Stream<Arguments> parameters() throws Throwable {

        return Stream.of(
            Arguments.of("소유한 쿠폰", null, "null", 14),
            Arguments.of("사용 가능한 쿠폰", true, "null", 7),
            Arguments.of("사용 불가능한 쿠폰", false, "null", 7),
            Arguments.of("가맹점 쿠폰, 매장 쿠폰이 모두 존재하는 곳에서 사용 가능한 쿠폰", true, "가맹점매장", 4),
            Arguments.of("가맹점 쿠폰, 매장 쿠폰이 모두 존재하는 곳에서 사용 불가능한 쿠폰", false, "가맹점매장", 6),
            Arguments.of("가맹점에서 사용 가능한 쿠폰", true, "가맹점", 3),
            Arguments.of("가맹점에서 사용 불가능한 쿠폰", false, "가맹점", 4),
            Arguments.of("개인 매장에서 사용 가능한 쿠폰", true, "매장", 5),
            Arguments.of("개인 매장에서 사용 불가능한 쿠폰", false, "매장", 3),
            Arguments.of("모든 매장 쿠폰 중 사용 가능한 쿠폰", true, "쿠폰없음", 2),
            Arguments.of("모든 매장 쿠폰 중 사용 불가능한 쿠폰", false, "쿠폰없음", 2)
        );
    }

    @Test
    @DisplayName("쿠폰 발급 테스트 성공")
    void modifyIssueCouponAccountSuccessTest() throws Exception {
        CouponPolicy couponPolicy = te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll());
        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);

        assertDoesNotThrow(
            () -> issueCouponRepository.provideCouponToAccount(issueCoupon.getCode(), LocalDate.now(), customer));

        em.flush();
        em.clear();

        IssueCoupon updateIssueCoupon = issueCouponRepository.findById(issueCoupon.getCode())
            .orElseThrow(IssueCouponNotFoundException::new);

        assertThat(updateIssueCoupon.getAccount().getId()).isEqualTo(customer.getId());
        assertThat(updateIssueCoupon.getExpirationDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("쿠폰 발급 테스트 실패 - 이미 유저에게 발급된 쿠폰")
    void modifyIssueCouponAccountFailTest() throws Exception {
        CouponPolicy couponPolicy = te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll());
        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);
        issueCoupon.provideToAccount(customer);

        assertThat(issueCouponRepository.provideCouponToAccount(issueCoupon.getCode(), LocalDate.now(), customer))
            .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 10, 15, 20, 25})
    @DisplayName("쿠폰 발급 기록 테스트 - 이전에 받은 적 있는 경우")
    void isReceivedBeforeTest(int minusDays) throws Exception {
        CouponPolicy couponPolicy = te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll());
        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);
        issueCoupon.provideToAccount(customer);
        ReflectionTestUtils.setField(issueCoupon, "receiptDate", LocalDate.now().minusDays(minusDays));

        boolean receivedBefore = issueCouponRepository.isReceivedBefore(
            couponPolicy.getId(), customer.getId(), couponPolicy.getUsagePeriod());

        assertThat(receivedBefore).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {30, 31, 32, 33, 100})
    @DisplayName("쿠폰 발급 기록 테스트 - 이전에 받은 적 있으나 기간이 지난 경우")
    void isReceivedBeforeButOverTest(int minusDays) throws Exception {
        CouponPolicy couponPolicy = te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll());
        IssueCoupon issueCoupon = te.getIssueCoupon(couponPolicy);
        issueCoupon.provideToAccount(customer);
        ReflectionTestUtils.setField(issueCoupon, "receiptDate", LocalDate.now().minusDays(minusDays));

        boolean receivedBefore = issueCouponRepository.isReceivedBefore(
            couponPolicy.getId(), customer.getId(), couponPolicy.getUsagePeriod());

        assertThat(receivedBefore).isFalse();
    }

    @Test
    @DisplayName("쿠폰 발급 기록 테스트 - 이전에 받은 적 없는 경우")
    void isNonReceivedBeforeTest() throws Exception {
        CouponPolicy couponPolicy = te.getCouponPolicy(te.getCouponTypeCash_1000_10000(), te.getCouponUsageAll());

        boolean receivedBefore = issueCouponRepository.isReceivedBefore(
            couponPolicy.getId(), customer.getId(), couponPolicy.getUsagePeriod());

        assertThat(receivedBefore).isFalse();
    }
}



