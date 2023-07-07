package store.cookshoong.www.cookshoongbackend.store.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@DataJpaTest
class HolidayRepositoryTest {

    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestEntityManager em;

    @Autowired
    HolidayRepository holidayRepository;

    @Test
    @DisplayName("휴업일 조회 - 성공")
    void find_holiday() {
        Store store = getOpenStore();

        Holiday actual = new Holiday(store, LocalDate.of(2020, 2, 20));

        em.persist(store);

        holidayRepository.save(actual);

        em.clear();

        List<Holiday> expect = holidayRepository.findByStore_Id(store.getId());

        assertThat(expect.size()).isEqualTo(1);
    }

    public Account getLevelOneCustomer() {
        Address address = em.persist(TestEntity.ADDRESS);
        AccountsStatus accountsStatus = em.persist(TestEntity.ACCOUNTS_STATUS_ACTIVE);
        Authority authority = em.persist(TestEntity.AUTHORITY_CUSTOMER);
        Rank rank = em.persist(TestEntity.RANK_LEVEL_ONE);
        return em.persist(TestEntity.ACCOUNT_ACTIVE_CUSTOMER_LEVEL_ONE);
    }

    public Store getOpenStore() {
        Account account = getLevelOneCustomer();
        BankType bankType = em.persist(TestEntity.BANK_TYPE_KB);
        StoreStatus storeStatus = em.persist(TestEntity.STORE_STATUS_OPEN);
        return em.persist(TestEntity.STORE_OPEN);
    }


}
