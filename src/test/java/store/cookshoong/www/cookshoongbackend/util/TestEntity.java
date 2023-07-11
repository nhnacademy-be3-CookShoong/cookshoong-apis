package store.cookshoong.www.cookshoongbackend.util;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLog;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponLogType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponType;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypePercent;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsage;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageAll;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageMerchant;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.payment.entity.Order;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;

/**
 * 테스트 환경에서 원하는 entity 미리 만들어두는 클래스.
 * 간편하게 재활용하여 사용할 수 있도록 함.
 *
 * @author eora21
 * @since 2023.07.07
 */
@TestComponent
@Import(TestEntityAspect.class)
public class TestEntity {
    public Address getAddress() {
        return new Address("main", "detail", BigDecimal.ONE, BigDecimal.ZERO);
    }

    public AccountsStatus getAccountStatusActive() {
        return new AccountsStatus("ACTIVE", "활성");
    }

    public Authority getAuthorityCustomer() {
        return new Authority("CUSTOMER", "일반회원");
    }

    public Rank getRankLevelOne() {
        return new Rank("LEVEL_1", "프렌드");
    }

    public Account getAccount(AccountsStatus accountsStatus, Authority authority, Rank rank) {
        return new Account(accountsStatus, authority, rank, "eora21", "pwd", "김주호",
            "말비묵", "test@test.com", LocalDate.of(1996, 4, 1),
            "01012345678");
    }

    public BankType getBankTypeKb() {
        return createTestBankType("KB", "국민은행");
    }

    public StoreStatus getStoreStatusOpen() {
        return createTestStoreStatus("OPEN", "영업중");
    }

    public Merchant getMerchant() {
        return new Merchant("네네치킨");
    }

    public Store getStore(Merchant merchant, Account account, BankType bankType, StoreStatus storeStatus) {
        return new Store(merchant, account, bankType,
            storeStatus, UUID.randomUUID() + ".jpg", "123456", "김주호",
            LocalDate.of(2020, 2, 20), "주호타코", "01012345678", BigDecimal.ONE,
            null, null, "123456");
    }

    public CouponTypePercent getCouponTypePercent_3_1000_10000() {
        return new CouponTypePercent(new BigDecimal("3"), 1_000, 10_000);
    }

    public CouponTypeCash getCouponTypeCash_1000_10000() {
        return new CouponTypeCash(1_000, 10_000);
    }

    public CouponUsageStore getCouponUsageStore(Store store) {
        return new CouponUsageStore(store);
    }

    public CouponUsageMerchant getCouponUsageMerchant(Merchant merchant) {
        return new CouponUsageMerchant(merchant);
    }

    public CouponUsageAll getCouponUsageAll() {
        return new CouponUsageAll();
    }

    public CouponPolicy getCouponPolicy(CouponType couponType, CouponUsage couponUsage) {
        return new CouponPolicy(couponType, couponUsage, "testCoupon", "just Test",
            LocalTime.of(1, 0, 0));
    }

    public IssueCoupon getIssueCoupon(CouponPolicy couponPolicy) {
        return new IssueCoupon(couponPolicy);
    }

    public CouponLogType getCouponLogType(String code, String description) {
        return createTestCouponLogType(code, description);
    }

    public CouponLog getCouponLog(IssueCoupon issueCoupon, CouponLogType couponLogType, Order order) {
        return new CouponLog(issueCoupon, couponLogType, order, LocalDateTime.now());
    }

    public Holiday getHoliday(Store store) {
        return new Holiday(store, LocalDate.of(2020, 2, 20));
    }

    public Order getOrder() {
        return createTestOrder();
    }

    private BankType createTestBankType(String bankTypeCode, String description) {
        BankType bankType = createEntityUsingDeclared(BankType.class);
        ReflectionTestUtils.setField(bankType, "bankTypeCode", bankTypeCode);
        ReflectionTestUtils.setField(bankType, "description", description);
        return bankType;
    }

    private StoreStatus createTestStoreStatus(String storeStatusCode, String description) {
        StoreStatus storeStatus = createEntityUsingDeclared(StoreStatus.class);
        ReflectionTestUtils.setField(storeStatus, "storeStatusCode", storeStatusCode);
        ReflectionTestUtils.setField(storeStatus, "description", description);
        return storeStatus;
    }

    private CouponLogType createTestCouponLogType(String code, String description) {
        CouponLogType couponLogType = createEntityUsingDeclared(CouponLogType.class);
        ReflectionTestUtils.setField(couponLogType, "code", code);
        ReflectionTestUtils.setField(couponLogType, "description", description);
        return couponLogType;
    }

    private Order createTestOrder() {
        Order order = createEntityUsingDeclared(Order.class);
        ReflectionTestUtils.setField(order, "orderCode", UUID.randomUUID());
        ReflectionTestUtils.setField(order, "orderedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "memo", "memo");
        return order;
    }

    private <T> T createEntityUsingDeclared(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
