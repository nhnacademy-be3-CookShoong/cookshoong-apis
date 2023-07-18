package store.cookshoong.www.cookshoongbackend.address.repository.accountaddress;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.address.entity.AccountAddress;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;

/**
 * AccountAddressRepository 에 대한 Test Code.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Slf4j
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

        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account account = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);

        Address address = new Address("광주 광역시 서석동", "조선대학교",
            new BigDecimal("23.5757577"), new BigDecimal("24.8898989"));

        em.persist(address);

        AccountAddress accountAddress = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "alias",
            LocalDateTime.now()
        );

        AccountAddress actual = accountAddressRepository.save(accountAddress);

        assertEquals(address.getMainPlace(), actual.getAddress().getMainPlace());
        assertEquals(accountAddress.getAlias(), actual.getAlias());
    }

    @Test
    @DisplayName("회원 아이디로 조회한 모든 주소")
    void getFindAccountByAccountId() {

        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account account = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);

        Address address = new Address("광주 광역시 서석동0", "조선대학교0",
            new BigDecimal("23.5757577"), new BigDecimal("24.8898989"));
        em.persist(address);

        AccountAddress accountAddress = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "기본0",
            LocalDateTime.now());
        accountAddressRepository.save(accountAddress);


        List<AccountAddressResponseDto> accountAddressList =
            accountAddressRepository.lookupByAccountIdAddressAll(account.getId());

        assertThat(accountAddressList.get(0).getMainPlace()).isEqualTo(accountAddress.getAddress().getMainPlace());
        assertThat(accountAddressList.get(0).getAlias()).isEqualTo(accountAddress.getAlias());

    }

    @Test
    @DisplayName("회원이 최근에 등록한 주소")
    void getFindAccountByAddressRecentRegistration() {

        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account account = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);

        Address address = new Address("광주 광역시 서석동0", "조선대학교0",
            new BigDecimal("23.5757577000000"), new BigDecimal("24.8898989000000"));
        em.persist(address);
        AccountAddress accountAddress = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "기본0",
            LocalDateTime.now()
            );
        accountAddressRepository.save(accountAddress);

        Address address1 = new Address("광주 광역시 서석동0", "조선대학교0",
            new BigDecimal("23.5757577000001"), new BigDecimal("24.8898989000001"));
        em.persist(address1);

        AccountAddress accountAddress1 = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "기본0",
            LocalDateTime.now()
            );
        accountAddressRepository.save(accountAddress1);


        AddressResponseDto actual =
            accountAddressRepository.lookupByAccountIdAddressRecentRegistration(account.getId());

        assertThat(actual.getMainPlace()).isEqualTo(accountAddress1.getAddress().getMainPlace());
        assertThat(actual.getDetailPlace()).isEqualTo(accountAddress1.getAddress().getDetailPlace());
        assertThat(actual.getLatitude()).isEqualTo(accountAddress1.getAddress().getLatitude());
        assertThat(actual.getLongitude()).isEqualTo(accountAddress1.getAddress().getLongitude());

    }

    @Test
    @DisplayName("회원이 주소록에 선택한 주소")
    void getLookupByAccountSelectAddressId() {

        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account account = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);

        Address address1 = new Address("광주 광역시 서석동0", "조선대학교0",
            new BigDecimal("23.5757577000001"), new BigDecimal("24.8898989000001"));
        em.persist(address1);

        AccountAddress accountAddress1 = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address1.getId()),
            account,
            address1,
            "기본0",
            LocalDateTime.now());
        accountAddressRepository.save(accountAddress1);


        AddressResponseDto actual =
            accountAddressRepository.lookupByAccountSelectAddressId(accountAddress1.getPk().getAddressId());

        log.info("ADDRESS: {}", actual);

        assertThat(actual.getMainPlace()).isEqualTo(accountAddress1.getAddress().getMainPlace());
        assertThat(actual.getDetailPlace()).isEqualTo(accountAddress1.getAddress().getDetailPlace());
        assertThat(actual.getLatitude()).isEqualTo(accountAddress1.getAddress().getLatitude());
        assertThat(actual.getLongitude()).isEqualTo(accountAddress1.getAddress().getLongitude());

    }

    @Test
    @DisplayName("회원으로 조회한 주소")
    void getFindByAccount() {

        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account account = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);

        Address address = new Address("광주 광역시 서석동0", "조선대학교0",
            new BigDecimal("23.5757577"), new BigDecimal("24.8898989"));
        em.persist(address);

        AccountAddress accountAddress = new AccountAddress(
            new AccountAddress.Pk(account.getId(), address.getId()),
            account,
            address,
            "기본0",
            LocalDateTime.now());
        accountAddressRepository.save(accountAddress);


        List<AccountAddress> accountAddressList = accountAddressRepository.findByAccount(account);

        assertThat(accountAddressList.get(0).getAddress().getMainPlace())
            .isEqualTo(accountAddress.getAddress().getMainPlace());
        assertThat(accountAddressList.get(0).getAlias()).isEqualTo(accountAddress.getAlias());

    }
}
