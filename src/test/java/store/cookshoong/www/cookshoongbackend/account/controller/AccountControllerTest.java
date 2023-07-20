package store.cookshoong.www.cookshoongbackend.account.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.exception.AuthorityNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.exception.SignUpValidationException;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountAuthResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountAuthDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountStatusDto;
import store.cookshoong.www.cookshoongbackend.account.service.AccountService;
import store.cookshoong.www.cookshoongbackend.address.model.request.CreateAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;

/**
 * 회원 컨트롤러 테스트.
 *
 * @author koesnam
 * @since 2023.07.06
 */
@AutoConfigureRestDocs
@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AccountService accountService;
    @MockBean
    AddressService addressService;

    SignUpRequestDto signUpRequestDto;
    CreateAccountAddressRequestDto createAccountAddressRequestDto;

    @BeforeEach
    void setup() {
        signUpRequestDto = ReflectionUtils.newInstance(SignUpRequestDto.class);
        createAccountAddressRequestDto = ReflectionUtils.newInstance(CreateAccountAddressRequestDto.class);

        ReflectionTestUtils.setField(createAccountAddressRequestDto, "alias", "NHN");
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "mainPlace", "경기도 성남시 분당구 대왕판교로645번길 16");
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "detailPlace", "NHN 플레이뮤지엄");
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "latitude", new BigDecimal("37.40096549041187"));
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "longitude", new BigDecimal("127.1040493631922"));

        ReflectionTestUtils.setField(signUpRequestDto, "loginId", "user1");
        ReflectionTestUtils.setField(signUpRequestDto, "password", "1234");
        ReflectionTestUtils.setField(signUpRequestDto, "name", "유유저");
        ReflectionTestUtils.setField(signUpRequestDto, "nickname", "이름이유저래");
        ReflectionTestUtils.setField(signUpRequestDto, "email", "user@cookshoong.store");
        ReflectionTestUtils.setField(signUpRequestDto, "birthday", LocalDate.of(1997, 6, 4));
        ReflectionTestUtils.setField(signUpRequestDto, "phoneNumber", "01012345678");
        ReflectionTestUtils.setField(signUpRequestDto, "createAccountAddressRequestDto", createAccountAddressRequestDto);
    }

    @Test
    @DisplayName("회원 등록 - 성공")
    void registerAccount() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts")
            .param("authorityCode", "customer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("registerAccount",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("account.Post"))
                    .requestFields(
                        fieldWithPath("loginId").description("로그인 때 사용되는 id"),
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("name").description("이름"),
                        fieldWithPath("nickname").description("별명"),
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("birthday").description("생일"),
                        fieldWithPath("phoneNumber").description("핸드폰 번호"),
                        fieldWithPath("createAccountAddressRequestDto.alias").description("별칭"),
                        fieldWithPath("createAccountAddressRequestDto.mainPlace").description("메인 주소"),
                        fieldWithPath("createAccountAddressRequestDto.detailPlace").description("상세 주소"),
                        fieldWithPath("createAccountAddressRequestDto.latitude").description("위도"),
                        fieldWithPath("createAccountAddressRequestDto.longitude").description("경도")
                    )));

        verify(accountService, times(1)).createAccount(any(SignUpRequestDto.class), eq(Authority.Code.CUSTOMER));
    }

    @Test
    @DisplayName("회원 등록 - 필수 필드에 값이 빈 값이 들어왔을 때")
    void registerAccount_2() throws Exception {
        ReflectionTestUtils.setField(signUpRequestDto, "loginId", null);

        RequestBuilder request = RestDocumentationRequestBuilders
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
    @DisplayName("회원 등록 - 권한 코드가 입력값에 없는 경우")
    void registerAccount_3() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
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

    @Test
    @DisplayName("회원 등록 - 유효하지 않은 권한 코드인 경우")
    void registerAccount_4() throws Exception {
        String invalidAuthorityCode = "user";

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts")
            .param("authorityCode", invalidAuthorityCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(AuthorityNotFoundException.class)
                .hasMessageContaining("존재하지 않는 권한")
            );

        verify(accountService, times(0)).createAccount(any(SignUpRequestDto.class), eq(Authority.Code.CUSTOMER));
    }

    @Test
    @DisplayName("회원 등록 - 이름이 숫자값으로만 이뤄져있는 경우(검증 실패)")
    void registerAccount_5() throws Exception {
        ReflectionTestUtils.setField(signUpRequestDto, "name", "1234");

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts")
            .param("authorityCode", Authority.Code.CUSTOMER.name())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andExpect(jsonPath("$.name").exists());

        verify(accountService, times(0)).createAccount(any(SignUpRequestDto.class), eq(Authority.Code.CUSTOMER));
    }

    @Test
    @DisplayName("회원 등록 - 중복된 아이디")
    void registerAccount_6() throws Exception {
        DuplicatedUserException exception = new DuplicatedUserException("mocked id");
        when(accountService.createAccount(any(SignUpRequestDto.class), any(Authority.Code.class)))
            .thenThrow(exception);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts")
            .param("authorityCode", Authority.Code.CUSTOMER.name())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(DuplicatedUserException.class)
                .hasStackTraceContaining("이미 존재하는 아이디")
            );

        verify(accountService, times(1)).createAccount(any(SignUpRequestDto.class), any(Authority.Code.class));
    }

    @Test
    @DisplayName("회원 조회 - (accountId 기준) 없는 회원 조회")
    void findAccount() throws Exception {
        UserNotFoundException exception = new UserNotFoundException();
        when(accountService.selectAccount(anyLong())).thenThrow(exception);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/{accountId}", Long.MAX_VALUE)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원")
            );
    }

    @Test
    @DisplayName("회원 조회 - (loginId 기준) 없는 회원 조회")
    void findAccount_2() throws Exception {
        UserNotFoundException exception = new UserNotFoundException();
        when(accountService.selectAccount(anyString())).thenThrow(exception);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/{loginId}/auth", "anonymous")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원"))
            .andDo(MockMvcRestDocumentationWrapper.document("findAccount",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("UserNotFoundException"))
                    .pathParameters(parameterWithName("loginId").description("로그인할 때 사용자 아이디"))
                    .responseFields(fieldWithPath("message").description("에러 메세지"))
                )
            );
    }

    @Test
    @DisplayName("회원 조회 - (loginId 기준) 있는 회원 조회")
    void findAccount_3() throws Exception {
        SelectAccountAuthDto testAuthDto = new SelectAccountAuthDto(1L, "나유저", "{bcrypt}1234",
            new Authority("CUSTOMER", "일반회원"),
            new AccountStatus("ACTIVE", "활성"));

        SelectAccountAuthResponseDto expect = SelectAccountAuthResponseDto.responseDtoFrom(testAuthDto);

        when(accountService.selectAccount(anyString())).thenReturn(expect);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/{loginId}/auth", expect.getLoginId())
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loginId").value(expect.getLoginId()))
            .andExpect(jsonPath("$.password").value(expect.getPassword()))
            .andExpect(jsonPath("$.attributes.accountId").value(testAuthDto.getId()))
            .andExpect(jsonPath("$.attributes.authority").value(testAuthDto.getAuthority().getAuthorityCode()))
            .andExpect(jsonPath("$.attributes.status").value(testAuthDto.getStatus().getStatusCode()))
            .andDo(MockMvcRestDocumentationWrapper.document("findAccount",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("SelectAccountAuthResponseDto"))
                    .pathParameters(
                        parameterWithName("loginId").description("로그인할 때 사용자 아이디"))
                    .responseFields(
                        fieldWithPath("loginId").description("사용자 아이디"),
                        fieldWithPath("password").description("사용자 비밀번호"),
                        fieldWithPath("attributes.accountId").description("사용자 시퀀스"),
                        fieldWithPath("attributes.status").description("사용자 상태"),
                        fieldWithPath("attributes.authority").description("사용자 권한")
                    ))
            );
    }

    @Test
    @DisplayName("회원상태 조회 - (accountId 기준) 있는 회원 조회")
    void findAccountStatus() throws Exception {
        SelectAccountStatusDto testStatusDto = new SelectAccountStatusDto(new AccountStatus("ACTIVE", "활성"));
        SelectAccountStatusResponseDto expect = SelectAccountStatusResponseDto.responseDtoFrom(testStatusDto);

        when(accountService.selectAccountStatus(anyLong())).thenReturn(expect);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/{loginId}/status", 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(expect.getStatus()))
            .andDo(MockMvcRestDocumentationWrapper.document("findAccountStatus",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("SelectAccountStatusResponseDto"))
                    .pathParameters(
                        parameterWithName("accountId").description("회원 시퀀스"))
                    .responseFields(
                        fieldWithPath("status").description("사용자 상태")
                    ))
            );
    }
}
