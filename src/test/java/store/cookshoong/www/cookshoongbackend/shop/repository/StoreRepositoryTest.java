package store.cookshoong.www.cookshoongbackend.shop.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoresHasCategory;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 가게 레포지토리 테스트 코드 작성.
 * 매장 등록, 존재여부, 사업자 입장에서 해당 매장 조회, 사용자 입장에서 해당 매장 조회, 매장리스트 조회(페이지)
 *
 * @author seungyeon (유승연)
 * @since 2023.07.14
 */
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class,
    TestEntityManager.class,
    TestPersistEntity.class})
class StoreRepositoryTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestPersistEntity tpe;
    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity testEntity;

    @Autowired
    StoreRepository storeRepository;

    List<Store> stores;

    Account account;
    Store store;


    @BeforeEach
    void setup() {
        stores = new ArrayList<>();
        account = tpe.getLevelOneActiveCustomer();
        em.persist(account);
        for (int i = 0; i < 3; i++) {
            store = tpe.getOpenStoreByOneAccount(account);
            em.persist(store);
            Address address = new Address("조선대학교 11번길", "33-1", new BigDecimal("111.0000000000000"), new BigDecimal("122.0000000000000"));
            em.persist(address);
            store.modifyAddress(address);
            stores.add(0, store);
        }
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("매장 저장 - 성공")
    void save_store() {
        Address address = new Address("조선대학교 11번길", "33-1", new BigDecimal(111), new BigDecimal(122));
        em.persist(address);
        Store expect = tpe.getOpenMerchantStore();
        expect.modifyAddress(address);
        Long storeId = storeRepository.save(expect).getId();

        Store actual = storeRepository.findById(storeId).orElseThrow();
        assertThat(actual.getId()).isEqualTo(storeId);
        assertThat(actual.getOpeningDate()).isEqualTo(expect.getOpeningDate());
        assertThat(actual.getName()).isEqualTo(expect.getName());
        assertThat(actual.getStoreStatus()).isEqualTo(expect.getStoreStatus());
        assertThat(actual.getDescription()).isEqualTo(expect.getDescription());
        assertThat(actual.getPhoneNumber()).isEqualTo(expect.getPhoneNumber());
        assertThat(actual.getRepresentativeName()).isEqualTo(expect.getRepresentativeName());
        assertThat(actual.getBusinessLicense()).isEqualTo(expect.getBusinessLicense());
        assertThat(actual.getMerchant()).isEqualTo(expect.getMerchant());
        assertThat(actual.getAccount()).isEqualTo(expect.getAccount());
    }

    @Test
    @DisplayName("매장이 존재 할 떄")
    void exist_store() {
        Store expect = em.find(Store.class, store.getId());

        boolean actual = storeRepository.existsStoreByBusinessLicenseNumber(expect.getBusinessLicenseNumber());

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("매장 존재하지 않을 때")
    void exist_store_fail() {
        Store expect = em.find(Store.class, store.getId());
        Address address = testEntity.getAddress();
        em.persist(address);
        expect.modifyAddress(address);

        boolean actual = storeRepository.existsStoreByBusinessLicenseNumber("000000000");

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("매장 조회 리스트 - 성공")
    void select_stores_page() {
        Account expectAccount = em.find(Account.class, account.getId());
        List<Store> expect = List.of(
            em.find(Store.class, store.getId()-2),
            em.find(Store.class, store.getId()-1),
            em.find(Store.class, store.getId())
        );
        List<SelectAllStoresResponseDto> selectAllStores =
            storeRepository.lookupStores(expectAccount.getId());

        assertThat(selectAllStores.size()).isEqualTo(3);
        for(int i= 0 ; i<3; i++){
            assertThat(selectAllStores.get(i).getStoreId()).isEqualTo(expect.get(i).getId());
            assertThat(selectAllStores.get(i).getStoreName()).isEqualTo(expect.get(i).getName());
            assertThat(selectAllStores.get(i).getStoreStatus()).isEqualTo(expect.get(i).getStoreStatus().getDescription());
            assertThat(selectAllStores.get(i).getStoreMainAddress()).isEqualTo(expect.get(i).getAddress().getMainPlace());
            assertThat(selectAllStores.get(i).getStoreDetailAddress()).isEqualTo(expect.get(i).getAddress().getDetailPlace());
        }
    }

    @Test
    @DisplayName("관리자 : 매장 한 개 조회 - 성공")
    void select_store() {
        Store expect = em.find(Store.class, store.getId());

        Long accountId = storeRepository.save(expect).getAccount().getId();
        SelectStoreResponseDto actual = storeRepository.lookupStore(accountId, expect.getId()).orElseThrow(StoreNotFoundException::new);

        assertThat(actual.getStoreName()).isEqualTo(expect.getName());
        assertThat(actual.getOpeningDate()).isEqualTo(expect.getOpeningDate());
        assertThat(actual.getDescription()).isEqualTo(expect.getDescription());
        assertThat(actual.getPhoneNumber()).isEqualTo(expect.getPhoneNumber());
        assertThat(actual.getBusinessLicenseNumber()).isEqualTo(expect.getBusinessLicenseNumber());
        assertThat(actual.getRepresentativeName()).isEqualTo(expect.getRepresentativeName());

        assertThat(actual.getLatitude()).isEqualTo(expect.getAddress().getLatitude());
        assertThat(actual.getLongitude()).isEqualTo(expect.getAddress().getLongitude());
        assertThat(actual.getMainPlace()).isEqualTo(expect.getAddress().getMainPlace());
        assertThat(actual.getDetailPlace()).isEqualTo(expect.getAddress().getDetailPlace());
        assertThat(actual.getBankCode()).isEqualTo(expect.getBankTypeCode().getDescription());
        assertThat(actual.getBankAccountNumber()).isEqualTo(expect.getBankAccountNumber());
    }

    @Test
    @DisplayName("사용자 : 매장에서 한 개 조회 - 성공")
    void select_store_for_user() {
        Store expect = em.find(Store.class, store.getId());

        SelectStoreForUserResponseDto actual = storeRepository.lookupStoreForUser(expect.getId()).orElseThrow(StoreNotFoundException::new);

        assertThat(actual.getStoreName()).isEqualTo(expect.getName());
        assertThat(actual.getDescription()).isEqualTo(expect.getDescription());
        assertThat(actual.getOpeningDate()).isEqualTo(expect.getOpeningDate());
        assertThat(actual.getPhoneNumber()).isEqualTo(expect.getPhoneNumber());
        assertThat(actual.getMainPlace()).isEqualTo(expect.getAddress().getMainPlace());
        assertThat(actual.getDetailPlace()).isEqualTo(expect.getAddress().getDetailPlace());
        assertThat(actual.getRepresentativeName()).isEqualTo(expect.getRepresentativeName());
    }

    @Test
    @DisplayName("사용자: 사용자 위치에서 3km 이내에 위치한 매장들만 조회")
    void select_Not_Outed_store() {
        List<Store> storeList = new ArrayList<>();

        Store expectFirst = em.find(Store.class, stores.get(0).getId());
        StoreCategory storeCategory = new StoreCategory("CHK", "치킨");
        em.persist(storeCategory);

        expectFirst.getStoresHasCategories().add(new StoresHasCategory(
            new StoresHasCategory.Pk(expectFirst.getId(), storeCategory.getCategoryCode()), expectFirst, storeCategory));

        storeList.add(expectFirst);

        Store expectSecond =  em.find(Store.class, stores.get(1).getId());

        expectSecond.getStoresHasCategories().add(new StoresHasCategory(
            new StoresHasCategory.Pk(expectSecond.getId(), storeCategory.getCategoryCode()), expectSecond, storeCategory));

        StoreStatus storeStatus1 = ReflectionUtils.newInstance(StoreStatus.class);
        ReflectionTestUtils.setField(storeStatus1, "code", "OUTED");
        ReflectionTestUtils.setField(storeStatus1, "description", "폐업");
        em.persist(storeStatus1);
        expectSecond.modifyStoreStatus(storeStatus1);

        storeList.add(expectSecond);

        Store expectThird =  em.find(Store.class, stores.get(2).getId());

        StoreCategory storeCategory1 = ReflectionUtils.newInstance(StoreCategory.class);
        ReflectionTestUtils.setField(storeCategory1, "categoryCode", "DER");
        ReflectionTestUtils.setField(storeCategory1, "description", "디저트");
        em.persist(storeCategory1);

        expectThird.getStoresHasCategories().add(new StoresHasCategory(
            new StoresHasCategory.Pk(expectSecond.getId(), storeCategory1.getCategoryCode()), expectThird, storeCategory1
        ));

        storeList.add(expectThird);

        Pageable pageable = PageRequest.of(0, 10);
        Page<SelectAllStoresNotOutedResponseDto> actual = storeRepository.lookupStoreLatLanPage(pageable);

        assertThat(actual.getTotalElements()).isEqualTo(2);
        assertThat(actual.getContent().get(0).getStoreStatus()).doesNotContain("OUTED");
    }
}
