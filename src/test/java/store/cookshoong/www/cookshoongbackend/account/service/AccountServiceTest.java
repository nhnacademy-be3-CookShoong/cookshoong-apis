package store.cookshoong.www.cookshoongbackend.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountsStatusRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AuthorityRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.RankRepository;

/**
 * 회원 서비스 테스트.
 *
 * @author koesnam
 * @since 2023.07.07
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    RankRepository rankRepository;
    @Mock
    AccountsStatusRepository accountsStatusRepository;
    @Mock
    AuthorityRepository authorityRepository;
    @InjectMocks
    AccountService accountService;

    static SignUpRequestDto testDto;
    static Authority testAuthority;
    static AccountsStatus testAccountsStatus;
    static Rank testRank;


    @BeforeEach
    void setup() {
        testDto = ReflectionUtils.newInstance(SignUpRequestDto.class);
        ReflectionTestUtils.setField(testDto, "loginId", "user1");
        ReflectionTestUtils.setField(testDto, "password", "1234");
        ReflectionTestUtils.setField(testDto, "name", "유유저");
        ReflectionTestUtils.setField(testDto, "nickname", "이름이유저래");
        ReflectionTestUtils.setField(testDto, "email", "user@cookshoong.store");
        ReflectionTestUtils.setField(testDto, "birthday", LocalDate.of(1997, 6, 4));
        ReflectionTestUtils.setField(testDto, "phoneNumber", "01012345678");

        testAuthority = new Authority("CUSTOMER", "일반 회원");
        testAccountsStatus = new AccountsStatus("ACTIVE", "활성");
        testRank = new Rank("LEVEL_1", "프랜드");
    }

    @Test
    @DisplayName("회원 저장 - 중복되는 ID로 저장하는 경우")
    void createAccount() {
        when(accountRepository.existsByLoginId(testDto.getLoginId())).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(testDto, Authority.Code.valueOf(testAuthority.getAuthorityCode())))
            .isInstanceOf(DuplicatedUserException.class)
            .hasMessageContaining("이미 존재하는 아이디");

        verify(accountRepository, atMostOnce()).existsByLoginId(testDto.getLoginId());
    }

    @Test
    @DisplayName("회원 저장 - 정상 저장")
    void createAccount_2() {
        Account testAccountAfterPersist = new Account(testAccountsStatus, testAuthority, testRank, testDto);
        ReflectionTestUtils.setField(testAccountAfterPersist, "id", 1L);

        when(accountRepository.existsByLoginId(testDto.getLoginId())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccountAfterPersist);
        when(accountsStatusRepository.getReferenceById(anyString())).thenReturn(testAccountsStatus);
        when(rankRepository.getReferenceById(anyString())).thenReturn(testRank);
        when(authorityRepository.getReferenceById(anyString())).thenReturn(testAuthority);

        Long actual = accountService.createAccount(testDto, Authority.Code.valueOf(testAuthority.getAuthorityCode()));
        assertThat(actual).isEqualTo(1L);

        verify(accountRepository, times(1)).existsByLoginId(anyString());
        verify(accountsStatusRepository, times(1)).getReferenceById(anyString());
        verify(rankRepository, times(1)).getReferenceById(anyString());
        verify(authorityRepository, times(1)).getReferenceById(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}

