package store.cookshoong.www.cookshoongbackend.shop.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * 가게 레포지토리 테스트 코드 작성.
 * 매장 등록, 존재여부, 사업자 입장에서 해당 매장 조회, 사용자 입장에서 해당 매장 조회, 매장리스트 조회(페이지)
 *
 * @author seungyeon
 * @since 2023.07.14
 */
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class,
    TestEntityManager.class})
class StoreRepositoryTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity testEntity;

    @Autowired
    StoreRepository storeRepository;

    static Store store;
    static Account account;
    static Merchant merchant;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        AccountStatus accountStatus = testEntity.getAccountStatusActive();
        Authority authority = new Authority("BUSINESS", "사업자회원");
        Rank rank = testEntity.getRankLevelOne();

        account = testEntity.getAccount(accountStatus, authority, rank);
        merchant = testEntity.getMerchant();

        StoreStatus storeStatus = testEntity.getStoreStatusOpen();
        BankType bankType = testEntity.getBankTypeKb();
        store = new Store(merchant, account, bankType, storeStatus, "businessLicenseImage1",
            "1234567891", "나기업", LocalDate.parse("1999-02-03"), "나기업의 김치찌개",
            "01088889991", new BigDecimal("1.1"), "우리 매장음식이 가장 맛있어요.", null, "11022223333");


        em.persist(accountStatus);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);
        em.persist(merchant);
        em.persist(storeStatus);
        em.persist(bankType);
    }

    @Test
    @DisplayName("매장 저장 - 성공")
    void save_store() {
        Address address = new Address("조선대학교 11번길", "33-1", new BigDecimal(111), new BigDecimal(122));
        em.persist(address);
        store.modifyAddress(address);
        Long storeId = storeRepository.save(store).getId();

        Store actual = storeRepository.findById(storeId).orElseThrow();
        assertThat(actual.getId()).isEqualTo(storeId);
        assertThat(actual.getOpeningDate()).isEqualTo(store.getOpeningDate());
        assertThat(actual.getName()).isEqualTo(store.getName());
        assertThat(actual.getStoreStatusCode()).isEqualTo(store.getStoreStatusCode());
        assertThat(actual.getDescription()).isEqualTo(store.getDescription());
        assertThat(actual.getPhoneNumber()).isEqualTo(store.getPhoneNumber());
        assertThat(actual.getRepresentativeName()).isEqualTo(store.getRepresentativeName());
        assertThat(actual.getBusinessLicense()).isEqualTo(store.getBusinessLicense());
        assertThat(actual.getMerchant()).isEqualTo(store.getMerchant());
        assertThat(actual.getAccount()).isEqualTo(store.getAccount());
        assertThat(actual.getImage()).isNull();
    }

    @Test
    @DisplayName("매장이 존재 할 떄")
    void exist_store() {
        storeRepository.save(store);

        boolean actual = storeRepository.existsStoreByBusinessLicenseNumber(store.getBusinessLicenseNumber());

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("매장 존재하지 않을 때")
    void exist_store_fail() {
        Address address = testEntity.getAddress();
        em.persist(address);
        store.modifyAddress(address);
        storeRepository.save(store);

        boolean actual = storeRepository.existsStoreByBusinessLicenseNumber("000000000");

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("매장 조회 (페이지) - 성공")
    void select_stores_page() {

        BankType bankType = em.find(BankType.class, "KB");
        StoreStatus storeStatus = em.find(StoreStatus.class, "OPEN");

        Address address = new Address("조선대학교 11번길", "33-1", new BigDecimal(111), new BigDecimal(122));
        em.persist(address);

        for (int i = 1; i < 10; i++) {
            Store store = new Store(merchant, account, bankType, storeStatus, "businessImage" + i,
                "1111111" + i, "유회장", LocalDate.of(2020, 11, 11), i + "호점",
                "01011112222", new BigDecimal("1.1"), "가장 맛있는 집", null, "011122222");
            store.modifyAddress(address);
            storeRepository.save(store);
        }
        Pageable pageable = PageRequest.of(2, 2);
        Page<SelectAllStoresResponseDto> actuals = storeRepository.lookupStoresPage(account.getId(), pageable);

        assertThat(actuals.get().count()).isEqualTo(2);
        assertThat(actuals.get().findFirst().get().getStoreName()).isEqualTo("5호점");
    }

    @Test
    @DisplayName("관리자 : 매장 한 개 조회 - 성공")
    void select_store() {
        Address address = new Address("조선대학교 11번길", "33-1", new BigDecimal("1.0000000"), new BigDecimal("1.0000000"));
        em.persist(address);
        store.modifyAddress(address);

        Long accountId = storeRepository.save(store).getId();
        Account account = accountRepository.findById(accountId).orElseThrow();
        SelectStoreResponseDto actual = storeRepository.lookupStore(accountId, store.getId()).orElseThrow();

        assertThat(actual.getLoginId()).isEqualTo(account.getLoginId());
        assertThat(actual.getStoreName()).isEqualTo(store.getName());
        assertThat(actual.getOpeningDate()).isEqualTo(store.getOpeningDate());
        assertThat(actual.getDescription()).isEqualTo(store.getDescription());
        assertThat(actual.getPhoneNumber()).isEqualTo(store.getPhoneNumber());
        assertThat(actual.getBusinessLicenseNumber()).isEqualTo(store.getBusinessLicenseNumber());
        assertThat(actual.getRepresentativeName()).isEqualTo(store.getRepresentativeName());

        assertThat(actual.getLatitude()).isEqualTo(store.getAddress().getLatitude());
        assertThat(actual.getLongitude()).isEqualTo(store.getAddress().getLongitude());
        assertThat(actual.getMainPlace()).isEqualTo(store.getAddress().getMainPlace());
        assertThat(actual.getDetailPlace()).isEqualTo(store.getAddress().getDetailPlace());
        assertThat(actual.getBankCode()).isEqualTo(store.getBankTypeCode().getDescription());
        assertThat(actual.getBankAccountNumber()).isEqualTo(store.getBankAccountNumber());
    }

    @Test
    @DisplayName("사용자 : 매장에서 한 개 조회 - 성공")
    void select_store_for_user() {
        Address address = new Address("조선대학교 11번길", "33-1", new BigDecimal("1.0000000"), new BigDecimal("1.0000000"));
        em.persist(address);

        store.modifyAddress(address);

        storeRepository.save(store);

        SelectStoreForUserResponseDto actual = storeRepository.lookupStoreForUser(store.getId()).orElseThrow();

        assertThat(actual.getStoreName()).isEqualTo(store.getName());
        assertThat(actual.getDescription()).isEqualTo(store.getDescription());
        assertThat(actual.getOpeningDate()).isEqualTo(store.getOpeningDate());
        assertThat(actual.getPhoneNumber()).isEqualTo(store.getPhoneNumber());
        assertThat(actual.getMainPlace()).isEqualTo(store.getAddress().getMainPlace());
        assertThat(actual.getDetailPlace()).isEqualTo(store.getAddress().getDetailPlace());
        assertThat(actual.getRepresentativeName()).isEqualTo(store.getRepresentativeName());
    }
}
