package store.cookshoong.www.cookshoongbackend.util;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
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
import store.cookshoong.www.cookshoongbackend.file.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.OrderStatus;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;

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

    public AccountStatus getAccountStatusActive() {
        return new AccountStatus("ACTIVE", "활성");
    }

    public Authority getAuthorityCustomer() {
        return new Authority("CUSTOMER", "일반회원");
    }

    public Rank getRankLevelOne() {
        return new Rank("LEVEL_1", "프렌드");
    }

    public Account getAccount(AccountStatus accountStatus, Authority authority, Rank rank) {
        return new Account(accountStatus, authority, rank, "eora21", "pwd", "김주호",
            "말비묵", "test@test.com", LocalDate.of(1996, 4, 1),
            "01012345678");
    }

    public BankType getBankTypeKb() {
        return createTestBankType("KB", "국민은행");
    }

    public ChargeType getChargeType() {
        return new ChargeType("KAKAO", "카카오결제", false);
    }

    public StoreStatus getStoreStatusOpen() {
        return createTestStoreStatus("OPEN", "영업중");
    }

    public Merchant getMerchant() {
        return new Merchant("네네치킨");
    }

    public Store getStore(Merchant merchant, Account account, BankType bankType,
                          StoreStatus storeStatus, Image businessImage, Image storeImage) {
        return new Store(merchant, account, bankType,
            storeStatus,businessImage, "123456", "김주호",
            LocalDate.of(2020, 2, 20), "주호타코", "01012345678", BigDecimal.ONE,
            null, storeImage, "123456");
    }

    public Image getImage(boolean isPublic){
        return createImage(isPublic);
    }
    public StoreCategory getStoreCategory() {
        return new StoreCategory("CHK", "치킨");
    }

    public CouponTypePercent getCouponTypePercent_3_1000_10000() {
        return new CouponTypePercent(3, 1_000, 10_000);
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
        return new CouponPolicy(couponType, couponUsage, "testCoupon", "just Test", 30);
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
        return new Holiday(store, LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 22));
    }

    public OrderStatus getOrderStatus(String orderStatusCode, String description) {
        return createTestOrderStatus(orderStatusCode, description);
    }

    public Order getOrder(Account account, Store store, OrderStatus orderStatus) {
        return createTestOrder(account, store, orderStatus);
    }

    private BankType createTestBankType(String bankTypeCode, String description) {
        BankType bankType = createUsingDeclared(BankType.class);
        ReflectionTestUtils.setField(bankType, "bankTypeCode", bankTypeCode);
        ReflectionTestUtils.setField(bankType, "description", description);
        return bankType;
    }

    private StoreStatus createTestStoreStatus(String storeStatusCode, String description) {
        StoreStatus storeStatus = createUsingDeclared(StoreStatus.class);
        ReflectionTestUtils.setField(storeStatus, "storeStatusCode", storeStatusCode);
        ReflectionTestUtils.setField(storeStatus, "description", description);
        return storeStatus;
    }

    private CouponLogType createTestCouponLogType(String code, String description) {
        CouponLogType couponLogType = createUsingDeclared(CouponLogType.class);
        ReflectionTestUtils.setField(couponLogType, "code", code);
        ReflectionTestUtils.setField(couponLogType, "description", description);
        return couponLogType;
    }

    private Order createTestOrder(Account account, Store store, OrderStatus orderStatus) {
        Order order = createUsingDeclared(Order.class);
        ReflectionTestUtils.setField(order, "orderCode", UUID.randomUUID());
        ReflectionTestUtils.setField(order, "orderedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "memo", "memo");
        ReflectionTestUtils.setField(order, "orderStatusCode", orderStatus);
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "account", account);
        return order;
    }

    private OrderStatus createTestOrderStatus(String orderStatusCode, String description){
        OrderStatus orderStatus = createUsingDeclared(OrderStatus.class);
        ReflectionTestUtils.setField(orderStatus, "orderStatusCode", orderStatusCode);
        ReflectionTestUtils.setField(orderStatus, "description", description);
        return orderStatus;
    }

    public Image createImage(boolean isPublic){
        Image image = createUsingDeclared(Image.class);
        ReflectionTestUtils.setField(image, "savedName", UUID.randomUUID()+".jpg");
        ReflectionTestUtils.setField(image, "isPublic", isPublic);
        return image;
    }


    public <T> T createUsingDeclared(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
