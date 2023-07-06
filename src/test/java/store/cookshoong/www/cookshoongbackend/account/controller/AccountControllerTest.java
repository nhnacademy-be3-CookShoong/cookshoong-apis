package store.cookshoong.www.cookshoongbackend.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.service.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 회원 컨트롤러 테스트.
 *
 * @author koesnam
 * @since 2023.07.06
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AccountService accountService;

    @Test
    @DisplayName("회원 등록 - 성공")
    void registerAccount() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        RequestBuilder request = MockMvcRequestBuilders
            .post("/api/accounts")
            .param("authorityCode", "customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated());

        verify(accountService, times(1)).createAccount(any(SignUpRequestDto.class), eq(Authority.Code.CUSTOMER));
    }

    @Test
    @DisplayName("회원 등록 - 필수 필드에 값이 빈 값이 들어왔을 때")
    void registerAccount_2() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            null);

        RequestBuilder request = MockMvcRequestBuilders
            .post("/api/accounts")
            .param("authorityCode", "customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(accountService, times(0)).createAccount(any(SignUpRequestDto.class), eq(Authority.Code.CUSTOMER));
    }

    @Test
    @DisplayName("회원 등록 - 권한 코드가 없는 경우")
    void registerAccount_3() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        RequestBuilder request = MockMvcRequestBuilders
            .post("/api/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(MissingServletRequestParameterException.class)
                .hasMessageContaining("authorityCode")
            );

        verify(accountService, times(0)).createAccount(any(SignUpRequestDto.class), eq(Authority.Code.CUSTOMER));
    }
}
