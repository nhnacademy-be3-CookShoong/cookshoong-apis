package store.cookshoong.www.cookshoongbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.Order;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.order.OrderStatus;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;

/**
 * TestEntity 내 미리 만들어 둔 entity 객체들이 필요한 타 객체들을 미리 em.persist 하여 쉽게 사용할 수 있도록 메서드를 모아두는 클래스.
 *
 * @author eora21
 * @since 2023.07.07
 */
@TestComponent
@EnableAspectJAutoProxy
@Import(TestEntity.class)
public class TestPersistEntity {
    @Autowired
    TestEntity testEntity;

    public Account getLevelOneActiveCustomer() {
        AccountStatus accountStatusActive = testEntity.getAccountStatusActive();
        Authority authorityCustomer = testEntity.getAuthorityCustomer();
        Rank rankLevelOne = testEntity.getRankLevelOne();
        return testEntity.getAccount(accountStatusActive, authorityCustomer, rankLevelOne);
    }

    public Store getOpenStore() {
        Account account = getLevelOneActiveCustomer();
        BankType bankTypeKb = testEntity.getBankTypeKb();
        StoreStatus storeStatusOpen = testEntity.getStoreStatusOpen();
        Image businessImage = testEntity.getImage("사업자등록증.jpg",false);
        Image storeImage = testEntity.getImage("매장사진.png",true);
        return testEntity.getStore(null, account, bankTypeKb, storeStatusOpen, businessImage, storeImage);
    }

    public Store getOpenStoreByOneAccount(Account account) {
        BankType bankTypeKb = testEntity.getBankTypeKb();
        StoreStatus storeStatusOpen = testEntity.getStoreStatusOpen();
        Image businessImage = testEntity.getImage("사업자등록증.jpg",false);
        Image storeImage = testEntity.getImage("매장사진.png",true);
        return testEntity.getStore(null, account, bankTypeKb, storeStatusOpen, businessImage, storeImage);
    }

    public Store getOpenMerchantStore() {
        Merchant merchant = testEntity.getMerchant();
        Account account = getLevelOneActiveCustomer();
        BankType bankTypeKb = testEntity.getBankTypeKb();
        StoreStatus storeStatusOpen = testEntity.getStoreStatusOpen();
        Image businessImage = testEntity.getImage("사업자등록증.png",false);
        Image storeImage = testEntity.getImage("매장시진.jpg",true);
        return testEntity.getStore(merchant, account, bankTypeKb, storeStatusOpen, businessImage, storeImage);
    }

    public IssueCoupon getIssueCoupon() {
        Store store = getOpenStore();
        CouponUsageStore couponUsageStore = testEntity.getCouponUsageStore(store);
        CouponTypeCash couponTypeCash = testEntity.getCouponTypeCash_1000_10000();
        CouponPolicy couponPolicy = testEntity.getCouponPolicy(couponTypeCash, couponUsageStore);
        return testEntity.getIssueCoupon(couponPolicy);
    }
    public Order createTestOrder(){
        Store store = getOpenStore();
        Account account = getLevelOneActiveCustomer();
        OrderStatus orderStatus = testEntity.getOrderStatus("CANCELED", "주문취소");
        return testEntity.getOrder(account, store, orderStatus);
    }
}
