package store.cookshoong.www.cookshoongbackend.account.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthAccount;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountInfoResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountAuthDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountStatusDto;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 회원 CRU 에 관한 테스트.
 *
 * @author koesnam
 * @since 2023.07.04
 */
@DataJpaTest
@Import({QueryDslConfig.class, TestPersistEntity.class})
class AccountRepositoryTest {
    @Autowired
    TestEntityManager em;

    @Autowired
    TestPersistEntity testPersistEntity;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("회원 조회 - 성공 로그인아이디 기준으로 조회")
    void findAccount() {
        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account expected = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        accountRepository.save(expected);

        em.clear();

        SelectAccountAuthDto actual = accountRepository.findByLoginId("user1").orElseThrow();

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getLoginId()).isEqualTo(expected.getLoginId());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
    }

    @Test
    @DisplayName("회원 조회 - 성공 accountId 기준으로 조회")
    void findAccount_2() {
        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account expected = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        Long accountId = accountRepository.save(expected).getId();

        em.clear();

        Account actual = accountRepository.findById(accountId).orElseThrow();

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getLoginId()).isEqualTo(expected.getLoginId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getBirthday()).isEqualTo(expected.getBirthday());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getNickname()).isEqualTo(expected.getNickname());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getLastLoginAt()).isEqualTo(expected.getLastLoginAt());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 조회 - 로그인 아이디 기준으로 없는 아이디 조회")
    void findAccount_3() {
        assertThatThrownBy(() -> accountRepository.findByLoginId("anonymous")
            .orElseThrow(UserNotFoundException::new))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원 조회 - accountId 기준으로 없는 아이디 조회")
    void findAccount_4() {
        assertThatThrownBy(() -> accountRepository.findById(Long.MAX_VALUE)
            .orElseThrow(UserNotFoundException::new))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("존재하지 않는 회원");
    }

    @Test
    @DisplayName("회원 저장 - 성공")
    void saveAccount() {
        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account expected = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        Long accountId = accountRepository.save(expected).getId();

        Account actual = em.find(Account.class, accountId);

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getLoginId()).isEqualTo(expected.getLoginId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getBirthday()).isEqualTo(expected.getBirthday());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getNickname()).isEqualTo(expected.getNickname());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getLastLoginAt()).isEqualTo(expected.getLastLoginAt());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 조회 - accountId를 이용한 모든 정보 조회")
    void lookup() {
        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account expected = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        Long accountId = accountRepository.save(expected).getId();

        SelectAccountResponseDto actual = accountRepository.lookupAccount(accountId).orElseThrow();

        assertThat(actual.getStatus()).isEqualTo(status.getDescription());
        assertThat(actual.getAuthority()).isEqualTo(authority.getDescription());
        assertThat(actual.getRank()).isEqualTo(rank.getName());
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getLoginId()).isEqualTo(expected.getLoginId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getBirthday()).isEqualTo(expected.getBirthday());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getNickname()).isEqualTo(expected.getNickname());
        assertThat(actual.getLastLoginAt()).isEqualTo(expected.getLastLoginAt());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 조회 - accountId를 이용한 모든 정보 조회 중 없는 회원일 때")
    void lookup_2() {
        Optional<SelectAccountResponseDto> expected = Optional.empty();

        Optional<SelectAccountResponseDto> actual = accountRepository.lookupAccount(Long.MAX_VALUE);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    @DisplayName("회원상태 조회 - accountId를 이용한 회원 상태만 조회")
    void lookup_accountStatus() {
        AccountStatus status = new AccountStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account expected = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        Long accountId = accountRepository.save(expected).getId();

        SelectAccountStatusDto actual = accountRepository.findAccountStatusById(accountId).orElseThrow();

        assertThat(actual.getStatus().getStatusCode()).isEqualTo(expected.getStatus().getStatusCode());
        assertThat(actual.getStatus().getDescription()).isEqualTo(expected.getStatus().getDescription());
    }

    @Test
    @DisplayName("OAuth2 회원 조회 - 해당 회원이 없는 경우")
    void lookupAccountInfoForOAuth() {
        String payco = "payco";
        String accountCode = "temp-account-code";

        Optional<SelectAccountInfoResponseDto> actual = accountRepository.lookupAccountInfoForOAuth(payco, accountCode);

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("OAuth2 회원 조회 - 해당 회원이 있는 경우")
    void lookupAccountInfoForOAuth_2() {
        String payco = "payco";
        String accountCode = "temp-account-code";

        OauthAccount expectPaycoAccount = testPersistEntity.getPaycoAccount(accountCode);
        testPersistEntity.getPaycoAccount("invalid-account-code");
        testPersistEntity.getPaycoAccount("super-invalid-account-code");

        Account expect = expectPaycoAccount.getAccount();
        SelectAccountInfoResponseDto actual = accountRepository.lookupAccountInfoForOAuth(payco, accountCode)
            .orElseThrow();

        assertAll(
            () -> assertThat(actual.getAccountId()).isEqualTo(expect.getId()),
            () -> assertThat(actual.getAuthority()).isEqualTo(expect.getAuthority().getAuthorityCode()),
            () -> assertThat(actual.getStatus()).isEqualTo(expect.getStatus().getStatusCode()),
            () -> assertThat(actual.getLoginId()).isEqualTo(expect.getLoginId())
        );
    }

    @Test
    @DisplayName("OAuth2 회원 조회 - 해당 회원이 없는 경우 - 공급자명 불일치")
    void lookupAccountInfoForOAuth_3() {
        String payco = "google";
        String accountCode = "temp-account-code";

        testPersistEntity.getPaycoAccount(accountCode);
        testPersistEntity.getPaycoAccount("invalid-account-code");
        testPersistEntity.getPaycoAccount("super-invalid-account-code");

        assertThatThrownBy(
            () -> accountRepository.lookupAccountInfoForOAuth(payco, accountCode).orElseThrow()
        );
    }

    @Test
    @DisplayName("OAuth2 회원 조회 - 해당 회원이 없는 경우 - 회원식별자 불일치")
    void lookupAccountInfoForOAuth_4() {
        String payco = "payco";
        String accountCode = "temp-account-code";

        testPersistEntity.getPaycoAccount("real-account-code");
        testPersistEntity.getPaycoAccount("invalid-account-code");
        testPersistEntity.getPaycoAccount("super-invalid-account-code");

        assertThatThrownBy(
            () -> accountRepository.lookupAccountInfoForOAuth(payco, accountCode).orElseThrow()
        );
    }
}
