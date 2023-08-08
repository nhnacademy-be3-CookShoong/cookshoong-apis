package store.cookshoong.www.cookshoongbackend.account.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.request.UpdateAccountInfoRequestDto;
import store.cookshoong.www.cookshoongbackend.account.service.AccountService;
import store.cookshoong.www.cookshoongbackend.config.IntegrationTestBase;

/**
 * account 관련된 통합테스트.
 *
 * @author koesnam (추만석)
 * @since 2023.08.07
 */
@AutoConfigureMockMvc
@Transactional
class AccountIntegrationTests extends IntegrationTestBase {
    @Autowired
    AccountService accountService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;
    @Autowired
    ObjectMapper objectMapper;
    SignUpRequestDto testSignUpRequestDto;
    Authority testAuthority;
    AccountStatus testAccountStatus;
    Rank testRank;
    Account testAccount;

    @BeforeEach
    void setup() {
        em.clear();
        testSignUpRequestDto = ReflectionUtils.newInstance(SignUpRequestDto.class);
        ReflectionTestUtils.setField(testSignUpRequestDto, "loginId", "user1");
        ReflectionTestUtils.setField(testSignUpRequestDto, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequestDto, "name", "유유저");
        ReflectionTestUtils.setField(testSignUpRequestDto, "nickname", "이름이유저래");
        ReflectionTestUtils.setField(testSignUpRequestDto, "email", "user@cookshoong.store");
        ReflectionTestUtils.setField(testSignUpRequestDto, "birthday", LocalDate.of(1997, 6, 4));
        ReflectionTestUtils.setField(testSignUpRequestDto, "phoneNumber", "01012345678");

        testAuthority = new Authority("CUSTOMER", "일반 회원");
        testAccountStatus = new AccountStatus("ACTIVE", "활성");
        testRank = new Rank("LEVEL_1", "프랜드");
        testAccount = new Account(testAccountStatus, testAuthority, testRank, testSignUpRequestDto);

        em.persist(testAuthority);
        em.persist(testRank);
        em.persist(testAccountStatus);
        em.persist(testAccount);
        em.flush();
    }

    @Test
    @DisplayName("회원 마지막 로그인 날짜 업데이트 - 기존 회원 로그인 성공시")
    void updateLastLoginDate() {
        LocalDateTime expect = testAccount.getLastLoginAt();
        accountService.updateLastLoginDate(testAccount.getId());
        LocalDateTime actual = em.getReference(Account.class, testAccount.getId()).getLastLoginAt();

        assertThat(actual).isAfter(expect);
    }

    @Test
    @DisplayName("회원 마지막 로그인 날짜 업데이트 - 기존 회원 로그인 성공시2")
    void updateLastLoginDate_2() throws Exception {
        LocalDateTime expect = testAccount.getLastLoginAt();

        RequestBuilder request = MockMvcRequestBuilders.put("/api/accounts/{accountId}/auth-success",
            testAccount.getId());

        mockMvc.perform(request)
            .andExpect(status().isOk());

        LocalDateTime actual = em.getReference(Account.class, testAccount.getId()).getLastLoginAt();

        assertThat(actual).isAfter(expect);
    }
}
