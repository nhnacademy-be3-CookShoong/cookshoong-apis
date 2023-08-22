package store.cookshoong.www.cookshoongbackend.payment.repository.refund;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.payment.entity.Refund;
import store.cookshoong.www.cookshoongbackend.payment.entity.RefundType;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * RefundRepository 에 대한 테스트 코드 작성.
 *
 * @author jeongjewan
 * @since 2023.08.06
 */
@Slf4j
@DataJpaTest
@Import({QueryDslConfig.class,
        TestEntity.class})
class RefundRepositoryTest {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    TestEntity tm;

    ChargeType chargeType;
    OrderStatus orderStatus;
    Store store;
    Account account;
    Merchant merchant;
    Image businessImage;
    Image storeImage;
    Order order;
    Charge charge;
    String paymentKey = "toss";
    UUID orderUuid = UUID.randomUUID();
    RefundType refundType;
    Refund refund;

    @BeforeEach
    void setup() {
        AccountStatus accountStatus = tm.getAccountStatusActive();
        Authority authority = new Authority("BUSINESS", "사업자회원");
        Rank rank = tm.getRankLevelOne();

        account = tm.getAccount(accountStatus, authority, rank);
        merchant = tm.getMerchant();

        StoreStatus storeStatus = tm.getStoreStatusOpen();
        BankType bankType = tm.getBankTypeKb();
        businessImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.BUSINESS_INFO_IMAGE.getVariable(), "사업자등록증.png", false);
        storeImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.STORE_IMAGE.getVariable(), "매장사진.png", true);
        store = tm.getStore(merchant, account, bankType, storeStatus, businessImage, storeImage);

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
        ReflectionTestUtils.setField(orderStatus, "code", "COMPLETE");
        ReflectionTestUtils.setField(orderStatus, "description", "주문완료");
        order = ReflectionUtils.newInstance(Order.class);
        ReflectionTestUtils.setField(order, "code", orderUuid);
        ReflectionTestUtils.setField(order, "orderStatus", orderStatus);
        ReflectionTestUtils.setField(order, "account", account);
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "orderedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "memo", "맛나게 해주세요");
        chargeType = new ChargeType("toss", "토스결제", false);

        em.persist(orderStatus);
        em.persist(order);
        em.persist(chargeType);

        charge = new Charge(chargeType, order, LocalDateTime.now(), 54000, paymentKey);
        em.persist(charge);

        refundType = new RefundType("partial", "부분환불", false);
        em.persist(refundType);
        refund = new Refund(refundType, charge, LocalDateTime.now(), 20000);
    }

    @Test
    @DisplayName("결제 취소에 대한 환불 데이터 저장 - 부분환불")
    void createRefundPartialSave() {
        Refund actual = refundRepository.save(refund);

        assertAll(
            () -> assertNotNull(actual),
            () -> assertEquals(refund.getRefundType(), actual.getRefundType()),
            () -> assertEquals(refund.getRefundAmount(), actual.getRefundAmount()),
            () -> assertEquals(refund.getRefundedAt(), actual.getRefundedAt())
        );
    }

    @Test
    @DisplayName("환불 totalAmount 가져오기")
    void getRefundTotalAmount() {
        refundRepository.save(refund);
        Refund refund2 = new Refund(refundType, charge, LocalDateTime.now(), 20000);
        refundRepository.save(refund2);

        Integer refundTotalAmount = refundRepository.findRefundTotalAmount(charge.getCode());

        assertEquals(refund.getRefundAmount() + refund2.getRefundAmount(), refundTotalAmount);
    }
}
