package store.cookshoong.www.cookshoongbackend.util;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.aspectj.weaver.ast.Or;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthAccount;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthType;
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
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;

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
    AtomicInteger atomicInt = new AtomicInteger();
    AtomicLong atomicLong = new AtomicLong();

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

    public OauthType getOauthTypePayco() {
        return getOauthType("payco");
    }

    public OauthType getOauthType(String provider) {
        OauthType oauthType = createUsingDeclared(OauthType.class);
        ReflectionTestUtils.setField(oauthType, "provider", provider);
        return oauthType;
    }

    public OauthAccount getOauthAccount(Account account, OauthType oauthType, String accountCode) {
        return new OauthAccount(account, oauthType, accountCode);
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
    public StoreStatus getStoreStatusClose() {
        return createTestStoreStatus("CLOSE", "휴식중");
    }

    public Merchant getMerchant() {
        return new Merchant("네네치킨");
    }

    public Store getStore(Merchant merchant, Account account, BankType bankType,
                          StoreStatus storeStatus, Image businessImage, Image storeImage) {
        CreateStoreRequestDto createStoreRequestDto = createStoreRequestDto(merchant, bankType);
        return new Store(merchant, account, bankType,
            storeStatus, businessImage, createStoreRequestDto, storeImage);
    }

    public Image getImage(String locationType, String domainName, String name, boolean isPublic) {
        return createImage(locationType, domainName, name, isPublic);
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
        return new CouponLog(issueCoupon, couponLogType, order);
    }

    public MenuStatus getMenuStatus(String menuStatusCode, String description) {
        return new MenuStatus(menuStatusCode, description);
    }


    public OrderStatus getOrderStatus(String statusCode, String description) {
        return createTestOrderStatus(statusCode, description);
    }

    public Order getOrder(Account account, Store store, OrderStatus orderStatus) {
        return createTestOrder(account, store, orderStatus);
    }

    public OrderDetail getOrderDetail(Order order, Menu menu, int count) {
        return new OrderDetail(order, menu, count, "현재메뉴이름", 2_000);
    }

    public OrderDetailMenuOption getOrderDetailMenuOption(Option option, OrderDetail orderDetail) {
        return new OrderDetailMenuOption(option, orderDetail, "현재옵션이름", 500);
    }

    public CreateStoreRequestDto getCreateStoreRequestDto(Merchant merchant, BankType bankType){
        return createStoreRequestDto(merchant, bankType);
    }

    public Menu getMenu(MenuStatus menuStatus, Store store, Image image, BigDecimal bigDecimal) {
        return new Menu(menuStatus, store, "메뉴", 2_000, "메뉴입니다.", image, 10,
            bigDecimal);
    }

    public OptionGroup getOptionGroup(Store store) {
        return new OptionGroup(store, "옵션그룹", 0, Integer.MAX_VALUE, false);
    }

    public Option getOption(OptionGroup optionGroup) {
        return new Option(optionGroup, "옵션", 1_000, 1, false);
    }

    public Charge getCharge(ChargeType chargeType, Order order) {
        return new Charge(chargeType, order, LocalDateTime.now(), 10_000, "paymentKey");
    }

    private CreateStoreRequestDto createStoreRequestDto(Merchant merchant, BankType bankType) {
        CreateStoreRequestDto createStoreRequestDto = createUsingDeclared(CreateStoreRequestDto.class);
        ReflectionTestUtils.setField(createStoreRequestDto, "merchantId", 1L);
        if(Objects.nonNull(merchant)){
            ReflectionTestUtils.setField(createStoreRequestDto, "merchantId", merchant.getId());
        }
        ReflectionTestUtils.setField(createStoreRequestDto, "businessLicenseNumber", "123456");
        ReflectionTestUtils.setField(createStoreRequestDto, "representativeName", "김주호");
        ReflectionTestUtils.setField(createStoreRequestDto, "openingDate", LocalDate.of(2020, 2, 20));
        ReflectionTestUtils.setField(createStoreRequestDto, "storeName", "주호타코");
        ReflectionTestUtils.setField(createStoreRequestDto, "mainPlace", "봉선2동 102길");
        ReflectionTestUtils.setField(createStoreRequestDto, "detailPlace", "20-18");
        ReflectionTestUtils.setField(createStoreRequestDto, "latitude", BigDecimal.ONE);
        ReflectionTestUtils.setField(createStoreRequestDto, "longitude", BigDecimal.ONE);
        ReflectionTestUtils.setField(createStoreRequestDto, "phoneNumber", "01012341234");
        ReflectionTestUtils.setField(createStoreRequestDto, "description", "타코집에 많이 와주세요");
        ReflectionTestUtils.setField(createStoreRequestDto, "earningRate", BigDecimal.ONE);
        ReflectionTestUtils.setField(createStoreRequestDto, "minimumOrderPrice", 0);
        ReflectionTestUtils.setField(createStoreRequestDto, "storeCategories", List.of("NIG"));
        ReflectionTestUtils.setField(createStoreRequestDto, "bankCode", bankType.getBankTypeCode());
        ReflectionTestUtils.setField(createStoreRequestDto, "bankAccount", "123456");
        ReflectionTestUtils.setField(createStoreRequestDto, "deliveryCost", 4000);
        return createStoreRequestDto;
    }

    private BankType createTestBankType(String bankTypeCode, String description) {
        BankType bankType = createUsingDeclared(BankType.class);
        ReflectionTestUtils.setField(bankType, "bankTypeCode", bankTypeCode);
        ReflectionTestUtils.setField(bankType, "description", description);
        return bankType;
    }

    private StoreStatus createTestStoreStatus(String code, String description) {
        StoreStatus storeStatus = createUsingDeclared(StoreStatus.class);
        ReflectionTestUtils.setField(storeStatus, "code", code);
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
        ReflectionTestUtils.setField(order, "code", UUID.randomUUID());
        ReflectionTestUtils.setField(order, "orderedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "memo", "memo");
        ReflectionTestUtils.setField(order, "orderStatus", orderStatus);
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "account", account);
        return order;
    }

    private OrderStatus createTestOrderStatus(String statusCode, String description){
        OrderStatus orderStatus = createUsingDeclared(OrderStatus.class);
        ReflectionTestUtils.setField(orderStatus, "code", statusCode);
        ReflectionTestUtils.setField(orderStatus, "description", description);
        return orderStatus;
    }

    public Image createImage(String locationType, String domainName, String name, boolean isPublic) {
        Image image = createUsingDeclared(Image.class);
        ReflectionTestUtils.setField(image, "locationType", locationType);
        ReflectionTestUtils.setField(image, "domainName", domainName);
        ReflectionTestUtils.setField(image, "originName", name);
        ReflectionTestUtils.setField(image, "savedName", UUID.randomUUID() + ".jpg");
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

    public <T> T persist(T t) {
        try {
            ReflectionTestUtils.setField(t, "id", atomicLong.getAndIncrement());
            return t;
        } catch (IllegalArgumentException e) {
            ReflectionTestUtils.setField(t, "id", atomicInt.getAndIncrement());
            return t;
        }
    }

    public <T> T persist(T t, long id) {
        ReflectionTestUtils.setField(t, "id", id);
        return t;
    }

    public long getLong() {
        return atomicLong.incrementAndGet();
    }

    public int getInt() {
        return atomicInt.incrementAndGet();
    }
}
