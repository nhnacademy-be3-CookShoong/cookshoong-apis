package store.cookshoong.www.cookshoongbackend.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
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
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * 회원 CRU 에 관한 테스트.
 *
 * @author koesnam
 * @since 2023.07.04
 */
@DataJpaTest
class AccountRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestEntityManager em;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("회원 조회 - 성공 로그인아이디 기준으로 조회")
    void findAccount() {
        AccountsStatus status = new AccountsStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account actual = new Account(status, authority, rank, "user1", "1234", "유유저",
                "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
                "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        accountRepository.save(actual);

        em.clear();

        Account expect = accountRepository.findByLoginId("user1").orElseThrow();

        assertThat(expect.getId()).isEqualTo(actual.getId());
        assertThat(expect.getLoginId()).isEqualTo(actual.getLoginId());
        assertThat(expect.getName()).isEqualTo(actual.getName());
        assertThat(expect.getBirthday()).isEqualTo(actual.getBirthday());
        assertThat(expect.getEmail()).isEqualTo(actual.getEmail());
        assertThat(expect.getNickname()).isEqualTo(actual.getNickname());
        assertThat(expect.getPassword()).isEqualTo(actual.getPassword());
        assertThat(expect.getLastLoginAt()).isEqualTo(actual.getLastLoginAt());
        assertThat(expect.getPhoneNumber()).isEqualTo(actual.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 조회 - 성공 accountId 기준으로 조회")
    void findAccount_2() {
        AccountsStatus status = new AccountsStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account actual = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        Long accountId = accountRepository.save(actual).getId();

        em.clear();

        Account expect = accountRepository.findById(accountId).orElseThrow();

        assertThat(expect.getId()).isEqualTo(actual.getId());
        assertThat(expect.getLoginId()).isEqualTo(actual.getLoginId());
        assertThat(expect.getName()).isEqualTo(actual.getName());
        assertThat(expect.getBirthday()).isEqualTo(actual.getBirthday());
        assertThat(expect.getEmail()).isEqualTo(actual.getEmail());
        assertThat(expect.getNickname()).isEqualTo(actual.getNickname());
        assertThat(expect.getPassword()).isEqualTo(actual.getPassword());
        assertThat(expect.getLastLoginAt()).isEqualTo(actual.getLastLoginAt());
        assertThat(expect.getPhoneNumber()).isEqualTo(actual.getPhoneNumber());
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
        AccountsStatus status = new AccountsStatus("ACTIVE", "활성");
        Authority authority = new Authority("USER", "일반회원");
        Rank rank = new Rank("LEVEL_4", "VIP");

        Account actual = new Account(status, authority, rank, "user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        em.persist(status);
        em.persist(authority);
        em.persist(rank);

        Long accountId = accountRepository.save(actual).getId();

        Account expect = em.find(Account.class, accountId);

        assertThat(expect.getId()).isEqualTo(actual.getId());
        assertThat(expect.getLoginId()).isEqualTo(actual.getLoginId());
        assertThat(expect.getName()).isEqualTo(actual.getName());
        assertThat(expect.getBirthday()).isEqualTo(actual.getBirthday());
        assertThat(expect.getEmail()).isEqualTo(actual.getEmail());
        assertThat(expect.getNickname()).isEqualTo(actual.getNickname());
        assertThat(expect.getPassword()).isEqualTo(actual.getPassword());
        assertThat(expect.getLastLoginAt()).isEqualTo(actual.getLastLoginAt());
        assertThat(expect.getPhoneNumber()).isEqualTo(actual.getPhoneNumber());
    }


}
