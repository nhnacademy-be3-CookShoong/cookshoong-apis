package store.cookshoong.www.cookshoongbackend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestComponent;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;

/**
 * TestEntity 내 미리 만들어 둔 entity 객체들이 필요한 타 객체들을 미리 em.persist 하여 쉽게 사용할 수 있도록 메서드를 모아두는 클래스.
 * 만약 DataJpaTest 내에서 사용 시 `@Import(TestPersistEntity.class)` 구문을 추가해주세요.
 *
 * @author eora21
 * @since 2023.07.07
 */
@TestComponent
public class TestPersistEntity {
    @Autowired
    TestEntityManager em;

    public Account getLevelOneCustomer() {
        AccountsStatus accountsStatus = em.persist(TestEntity.getAccountStatusActive());
        Authority authority = em.persist(TestEntity.getAuthorityCustomer());
        Rank rank = em.persist(TestEntity.getRankLevelOne());
        return em.persist(TestEntity.getAccount(accountsStatus, authority, rank));
    }

    public Store getOpenStore() {
        Account account = getLevelOneCustomer();
        BankType bankType = em.persist(TestEntity.getBankTypeKb());
        StoreStatus storeStatus = em.persist(TestEntity.getStoreStatusOpen());
        return em.persist(TestEntity.getStore(account, bankType, storeStatus));
    }
}
