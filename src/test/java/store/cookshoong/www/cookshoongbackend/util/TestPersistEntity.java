package store.cookshoong.www.cookshoongbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponTypeCash;
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponUsageStore;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;

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
        AccountsStatus accountStatusActive = testEntity.getAccountStatusActive();
        Authority authorityCustomer = testEntity.getAuthorityCustomer();
        Rank rankLevelOne = testEntity.getRankLevelOne();
        return testEntity.getAccount(accountStatusActive, authorityCustomer, rankLevelOne);
    }

    public Store getOpenStore() {
        Account account = getLevelOneActiveCustomer();
        BankType bankTypeKb = testEntity.getBankTypeKb();
        StoreStatus storeStatusOpen = testEntity.getStoreStatusOpen();
        return testEntity.getStore(account, bankTypeKb, storeStatusOpen);
    }

    public IssueCoupon getIssueCoupon() {
        Store store = getOpenStore();
        CouponUsageStore couponUsageStore = testEntity.getCouponUsageStore(store);
        CouponTypeCash couponTypeCash = testEntity.getCouponTypeCash_1000_10000();
        CouponPolicy couponPolicy = testEntity.getCouponPolicy(couponTypeCash, couponUsageStore);
        return testEntity.getIssueCoupon(couponPolicy);
    }
}
