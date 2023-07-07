package store.cookshoong.www.cookshoongbackend.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.AuthorityCode;
import store.cookshoong.www.cookshoongbackend.account.service.AccountService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    void registerAccount() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("user1", "1234", "유유저",
            "이름이유저래", "user@cookshoong.store", LocalDate.of(1997, 6, 4),
            "01012345678");

        RequestBuilder request = MockMvcRequestBuilders
            .post("/api/accounts/{authorityCode}", "customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated());

        verify(accountService, times(1)).createAccount(any(SignUpRequestDto.class), eq(AuthorityCode.CUSTOMER));
    }
}
