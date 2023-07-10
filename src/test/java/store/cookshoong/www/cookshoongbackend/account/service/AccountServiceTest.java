package store.cookshoong.www.cookshoongbackend.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountStatusRepository;
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
    AccountStatusRepository accountStatusRepository;
    @Mock
    AuthorityRepository authorityRepository;
    @InjectMocks
    AccountService accountService;

    static SignUpRequestDto testDto;
    static Authority testAuthority;
    static AccountStatus testAccountStatus;
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
        testAccountStatus = new AccountStatus("ACTIVE", "활성");
        testRank = new Rank("LEVEL_1", "프랜드");
    }

    @Test
    @DisplayName("회원 저장 - 중복되는 ID로 저장하는 경우")
    void createAccount() {
        Authority.Code authorityCode = Authority.Code.valueOf(testAuthority.getAuthorityCode());

        when(accountRepository.existsByLoginId(testDto.getLoginId())).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(testDto, authorityCode))
            .isInstanceOf(DuplicatedUserException.class)
            .hasMessageContaining("이미 존재하는 아이디");

        verify(accountRepository, atMostOnce()).existsByLoginId(testDto.getLoginId());
    }

    @Test
    @DisplayName("회원 저장 - 정상 저장")
    void createAccount_2() {
        Account testAccountAfterPersist = new Account(testAccountStatus, testAuthority, testRank, testDto);
        ReflectionTestUtils.setField(testAccountAfterPersist, "id", 1L);

        when(accountRepository.existsByLoginId(testDto.getLoginId())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccountAfterPersist);
        when(accountStatusRepository.getReferenceById(anyString())).thenReturn(testAccountStatus);
        when(rankRepository.getReferenceById(anyString())).thenReturn(testRank);
        when(authorityRepository.getReferenceById(anyString())).thenReturn(testAuthority);

        Long actual = accountService.createAccount(testDto, Authority.Code.valueOf(testAuthority.getAuthorityCode()));
        assertThat(actual).isEqualTo(1L);

        verify(accountRepository, times(1)).existsByLoginId(anyString());
        verify(accountStatusRepository, times(1)).getReferenceById(anyString());
        verify(rankRepository, times(1)).getReferenceById(anyString());
        verify(authorityRepository, times(1)).getReferenceById(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("회원 조회 - accountId 이용한 회원 조회중 없는 회원을 조회")
    void selectAccount() {
        when(accountRepository.lookupAccount(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.selectAccount(Long.MAX_VALUE))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("존재하지 않는 회원");

        verify(accountRepository, times(1)).lookupAccount(anyLong());
    }

    @Test
    @DisplayName("회원 조회 - accountId 이용한 회원 정보 조회")
    void selectAccount_2() {
        SelectAccountResponseDto expect = new SelectAccountResponseDto(
            1L, testAccountStatus.getDescription(), testAuthority.getDescription(),
            testRank.getName(), testDto.getLoginId(), testDto.getName(),
            testDto.getNickname(), testDto.getEmail(), testDto.getBirthday(),
            testDto.getPhoneNumber(), LocalDateTime.now()
        );

        when(accountRepository.lookupAccount(expect.getId())).thenReturn(Optional.of(expect));

        SelectAccountResponseDto actual = accountService.selectAccount(expect.getId());

        assertThat(actual.getId()).isEqualTo(expect.getId());
        assertThat(actual.getStatus()).isEqualTo(expect.getStatus());
        assertThat(actual.getAuthority()).isEqualTo(expect.getAuthority());
        assertThat(actual.getRank()).isEqualTo(expect.getRank());
        assertThat(actual.getName()).isEqualTo(expect.getName());
        assertThat(actual.getNickname()).isEqualTo(expect.getNickname());
        assertThat(actual.getEmail()).isEqualTo(expect.getEmail());
        assertThat(actual.getBirthday()).isEqualTo(expect.getBirthday());
        assertThat(actual.getPhoneNumber()).isEqualTo(expect.getPhoneNumber());

        verify(accountRepository, times(1)).lookupAccount(anyLong());
    }
}

