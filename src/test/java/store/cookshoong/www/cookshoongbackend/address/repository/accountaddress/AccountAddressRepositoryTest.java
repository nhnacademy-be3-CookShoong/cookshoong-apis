package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.address.entity.AccountAddress;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AccountAddressRepository 에 대한 Test Code.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@DataJpaTest
@Import(QueryDslConfig.class)
class AccountAddressRepositoryTest {

    @Autowired
    private AccountAddressRepository accountAddressRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("회원으로 주소 등록")
    void createAccountAddress() {

        Rank actual1 = new Rank("VIP", "VIP");
        AccountsStatus actual2 = new AccountsStatus("ACTIVE", "활성");
        Authority actual3 = new Authority("USER", "일반회원");


        Account account = new Account(actual1, actual2, actual3, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(actual1);
        em.persist(actual2);
        em.persist(actual3);
        em.persist(account);

        Address address = new Address("광주 광역시 서석동", "조선대학교", new BigDecimal("23.5757577"), new BigDecimal("24.8898989"));

        em.persist(address);

        AccountAddress accountAddress = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "alias"
        );

        AccountAddress actual = accountAddressRepository.save(accountAddress);

        assertEquals(address.getMainPlace(), actual.getAddress().getMainPlace());
        assertEquals(accountAddress.getAlias(), actual.getAlias());
    }

    @Test
    @DisplayName("회원 아이디로 조회한 주소")
    void getFindAccountByAccountId() {

        Rank rank = new Rank("VIP", "VIP");
        AccountsStatus accountsStatus = new AccountsStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");

        Account account = new Account(rank, accountsStatus, authority, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(rank);
        em.persist(accountsStatus);
        em.persist(authority);

        em.persist(account);

        Address address = new Address("광주 광역시 서석동0", "조선대학교0", new BigDecimal("23.5757577"), new BigDecimal("24.8898989"));
        em.persist(address);

        AccountAddress accountAddress = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "기본0");
        accountAddressRepository.save(accountAddress);


        List<AccountAddressResponseDto> accountAddressList = accountAddressRepository.getByAccountIdAddress(account.getId());

        assertThat(accountAddressList.get(0).getMainAddress()).isEqualTo(accountAddress.getAddress().getMainPlace());
        assertThat(accountAddressList.get(0).getAlias()).isEqualTo(accountAddress.getAlias());

    }



}
