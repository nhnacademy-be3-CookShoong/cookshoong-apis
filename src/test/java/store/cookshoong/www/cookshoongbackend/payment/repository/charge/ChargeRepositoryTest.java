package store.cookshoong.www.cookshoongbackend.payment.repository.charge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.OrderStatus;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * Charge Repository 테스트.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@Slf4j
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class})
class ChargeRepositoryTest {

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity tm;

    ChargeType chargeType;
    OrderStatus orderStatus;
    Store store;
    Account account;
    Merchant merchant;
    Image businessImage;
    Image storeImage;
    Image menuImage;
    MenuStatus menuStatus;
    Menu menu;
    Order order;
    Charge charge;
//    String paymentKey = "2WkABYDxNyJQbgMGZzorzYWwKBG9kVl5E1em4dKva7XL9njP";
    String paymentKey = "toss";
    Charge actual;
    UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setup() {
        AccountStatus accountStatus = tm.getAccountStatusActive();
        Authority authority = new Authority("BUSINESS", "사업자회원");
        Rank rank = tm.getRankLevelOne();

        account = tm.getAccount(accountStatus, authority, rank);
        merchant = tm.getMerchant();

        StoreStatus storeStatus = tm.getStoreStatusOpen();
        BankType bankType = tm.getBankTypeKb();
        businessImage = tm.getImage("사업자등록증.png", false);
        storeImage = tm.getImage("매장사진.png", true);
        store = new Store(merchant, account, bankType, storeStatus, businessImage,
            "1234567891", "나기업", LocalDate.parse("1999-02-03"), "나기업의 김치찌개",
            "01088889991", new BigDecimal("1.1"), "우리 매장음식이 가장 맛있어요.", storeImage, "11022223333");

        em.persist(accountStatus);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);
        em.persist(merchant);
        em.persist(storeStatus);
        em.persist(bankType);
        em.persist(businessImage);
        em.persist(storeImage);
        em.persist(store);

        orderStatus = ReflectionUtils.newInstance(OrderStatus.class);
        ReflectionTestUtils.setField(orderStatus, "orderStatusCode", "COMPLETE");
        ReflectionTestUtils.setField(orderStatus, "description", "주문완료");
        order = ReflectionUtils.newInstance(Order.class);
        ReflectionTestUtils.setField(order, "orderCode", uuid);
        ReflectionTestUtils.setField(order, "orderStatusCode", orderStatus);
        ReflectionTestUtils.setField(order, "account", account);
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "orderedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "memo", "맛나게 해주세요");
        chargeType = new ChargeType("toss", "토스결제", false);

        em.persist(orderStatus);
        em.persist(order);
        em.persist(chargeType);

        charge = new Charge(chargeType, order, LocalDateTime.now(), 54000, paymentKey);



        log.info("order uuid = {}", order.getOrderCode());
        log.info("uuid = {}", uuid);
        log.info("charge = {}", charge.getOrder().getOrderCode());
    }

    @Test
    @DisplayName("결제 승인 후 결제 완료된 정보 저장")
    void paymentSave() {
        actual = chargeRepository.save(charge);

        assertNotNull(actual);
        assertEquals(charge.getChargedAt(), actual.getChargedAt());
        assertEquals(charge.getChargedAmount(), actual.getChargedAmount());
        assertEquals(charge.getChargeType(), actual.getChargeType());
        assertEquals(charge.getOrder(), actual.getOrder());
        assertEquals(charge.getPaymentKey(), actual.getPaymentKey());
    }

    @Test
    @DisplayName("orderId 를 통해 결제에서 PaymentKey 가져오기")
    void lookupFindByPaymentKey() {
        actual = chargeRepository.save(charge);

        String expectedPaymentKey = paymentKey; // Set the expected paymentKey here

        String actualPaymentKey = chargeRepository.lookupFindByPaymentKey(order.getOrderCode());

        assertEquals(expectedPaymentKey, actualPaymentKey);
    }
}
