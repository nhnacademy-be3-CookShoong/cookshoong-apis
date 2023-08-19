package store.cookshoong.www.cookshoongbackend.account.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.exception.AccountStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.AuthorityNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.exception.SignUpValidationException;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.request.OAuth2SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountAuthResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountInfoResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.UpdateAccountStatusResponseDto;
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
                    .requestSchema(Schema.schema("registerAccount.Request"))
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

    @ParameterizedTest
    @DisplayName("회원상태 변경 - 유효한 상태로 변경")
    @ValueSource(strings = {"dormancy", "DORMANCY"})
    void putAccountStatus(String code) throws Exception {
        String expectDescription = "휴면";
        UpdateAccountStatusResponseDto response = new UpdateAccountStatusResponseDto(expectDescription,
            LocalDateTime.now());
        when(accountService.updateAccountStatus(anyLong(), anyString())).thenReturn(response);

        RequestBuilder request = RestDocumentationRequestBuilders
            .put("/api/accounts/{loginId}/status", 1L)
            .queryParam("code", code);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(expectDescription))
            .andDo(MockMvcRestDocumentationWrapper.document("findAccountStatus",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("SelectAccountStatusResponseDto"))
                    .pathParameters(
                        parameterWithName("accountId").description("회원 시퀀스"))
                    .requestParameters(
                        parameterWithName("code").description("변경할 상태")
                    )
                    .responseFields(
                        fieldWithPath("status").description("사용자상태"),
                        fieldWithPath("updatedAt").description("변경일시")
                    ))
            );
    }

    @ParameterizedTest
    @DisplayName("회원상태 변경 - 유효하지 않는 상태로 변경")
    @ValueSource(strings = {"domacy", "inactive", "with"})
    void putAccountStatus_2(String code) throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .put("/api/accounts/{loginId}/status", 1L)
            .queryParam("code", code);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(AccountStatusNotFoundException.class)
                .hasMessageContaining("존재하지 않는 상태")
                .hasMessageContaining(code));
    }

    @ParameterizedTest
    @DisplayName("회원상태 변경 - 상태코드가 아닌 설명으로의 변경")
    @ValueSource(strings = {"휴면", "탈퇴", "활성"})
    void putAccountStatus_3(String code) throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .put("/api/accounts/{loginId}/status", 1L)
            .queryParam("code", code);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(AccountStatusNotFoundException.class)
                .hasMessageContaining("존재하지 않는 상태")
                .hasMessageContaining(code));
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 정상 등록")
    void postOAuth2Account() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("postOAuth2Account",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("postOAuth2Account.Request"))
                    .requestFields(
                        fieldWithPath("provider").description("OAuth 공급자명"),
                        fieldWithPath("accountCode").description("OAuth 공급자로부터 부여된 회원식별자"),
                        fieldWithPath("signUpRequestDto.loginId").description("로그인 때 사용되는 id"),
                        fieldWithPath("signUpRequestDto.password").description("비밀번호"),
                        fieldWithPath("signUpRequestDto.name").description("이름"),
                        fieldWithPath("signUpRequestDto.nickname").description("별명"),
                        fieldWithPath("signUpRequestDto.email").description("이메일"),
                        fieldWithPath("signUpRequestDto.birthday").description("생일"),
                        fieldWithPath("signUpRequestDto.phoneNumber").description("핸드폰 번호"),
                        fieldWithPath("signUpRequestDto.createAccountAddressRequestDto.alias").description("별칭"),
                        fieldWithPath("signUpRequestDto.createAccountAddressRequestDto.mainPlace").description("메인 주소"),
                        fieldWithPath("signUpRequestDto.createAccountAddressRequestDto.detailPlace").description("상세 주소"),
                        fieldWithPath("signUpRequestDto.createAccountAddressRequestDto.latitude").description("위도"),
                        fieldWithPath("signUpRequestDto.createAccountAddressRequestDto.longitude").description("경도")
                    )));

    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - OAuth 공급자명 누락")
    void postOAuth2Account_2() throws Exception {
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            null);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andExpect(jsonPath("$.provider").value("must not be blank"));
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - OAuth 공급자로부터 부여된 회원식별자 누락")
    void postOAuth2Account_3() throws Exception {
        String expectProvider = "payco";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, null,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andExpect(jsonPath("$.accountCode").value("must not be blank"));
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 회원정보 객체 누락")
    void postOAuth2Account_4() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(null, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andExpect(jsonPath("$.signUpRequestDto").value("must not be null"));
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 회원정보 중 필드 누락")
    void postOAuth2Account_5() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        ReflectionTestUtils.setField(signUpRequestDto, "loginId", null);
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));


        MvcResult actual = mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andReturn();

        String actualMessage = actual.getResponse().getContentAsString();
        assertAll(
            () -> assertThat(actualMessage).contains("loginId"),
            () -> assertThat(actualMessage).contains("must not be blank")
        );
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 회원정보 중 주소 객체 누락")
    void postOAuth2Account_6() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        ReflectionTestUtils.setField(signUpRequestDto, "createAccountAddressRequestDto", null);
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));


        MvcResult actual = mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andReturn();

        String actualMessage = actual.getResponse().getContentAsString();
        assertAll(
            () -> assertThat(actualMessage).contains("createAccountAddressRequestDto"),
            () -> assertThat(actualMessage).contains("must not be null")
        );
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 회원정보 중 주소객체의 필드 누락")
    void postOAuth2Account_7() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";

        ReflectionTestUtils.setField(createAccountAddressRequestDto, "alias", null);
        ReflectionTestUtils.setField(signUpRequestDto, "createAccountAddressRequestDto", createAccountAddressRequestDto);

        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));


        MvcResult actual = mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException())
                .isInstanceOf(SignUpValidationException.class))
            .andReturn();

        String actualMessage = actual.getResponse().getContentAsString();
        assertAll(
            () -> assertThat(actualMessage).contains("signUpRequestDto.createAccountAddressRequestDto.alias"),
            () -> assertThat(actualMessage).contains("must not be blank")
        );
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 서비스 코드 호출순서 확인 - 회원이 생성된 후 주소가 생성되어야 한다.")
    void postOAuth2Account_8() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isCreated());

        InOrder inOrder = Mockito.inOrder(accountService, addressService);
        inOrder.verify(accountService).createAccount(any(), any());
        inOrder.verify(addressService).createAccountAddress(any(), any());
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 서비스 코드 호출순서 확인 - 회원이 생성된 후 OAuth 회원이 생성되어야 한다.")
    void postOAuth2Account_9() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isCreated());

        InOrder inOrder = Mockito.inOrder(accountService, addressService);
        inOrder.verify(accountService).createAccount(any(), any());
        inOrder.verify(accountService).createOAuth2Account(any(), any(), any());
    }

    @Test
    @DisplayName("OAuth2를 이용한 회원가입 - 서비스 코드 호출횟수 확인 - 각 서비스 객체의 메서드는 한번씩만 호출되어야 한다.")
    void postOAuth2Account_10() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        OAuth2SignUpRequestDto expectDto = new OAuth2SignUpRequestDto(signUpRequestDto, expectAccountCode,
            expectProvider);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/accounts/oauth2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(expectDto));

        mockMvc.perform(request)
            .andExpect(status().isCreated());

        InOrder inOrder = Mockito.inOrder(accountService, addressService);
        inOrder.verify(accountService, calls(1)).createAccount(any(), any());
        inOrder.verify(addressService, calls(1)).createAccountAddress(any(), any());
        inOrder.verify(accountService, calls(1)).createOAuth2Account(any(), any(), any());
    }


    @Test
    @DisplayName("가입된 OAuth2 유저 조회 - 회원식별번호와 OAuth2 공급자 기준으로 조회")
    void getAccountInfoForOAuth() throws Exception {
        String expectProvider = "payco";
        String expectAccountCode = "f32klsdjf923-fdsf32fs-dsf3q2ad-fsfwe";
        SelectAccountInfoResponseDto expectDto = new SelectAccountInfoResponseDto(1L, "Test1", "CUSTOMER", "ACTIVE");

        when(accountService.selectAccountInfoForOAuth(expectProvider, expectAccountCode)).thenReturn(expectDto);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/oauth2")
            .param("provider", expectProvider)
            .param("accountCode", expectAccountCode);

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getAccountInfoForOAuth",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("getAccountInfoForOAuth.Request"))
                    .requestParameters(
                        parameterWithName("provider").description("OAuth2 공급자"),
                        parameterWithName("accountCode").description("OAuth2 공급자로부터 부여된 회원식별자")
                    )
                    .responseSchema(Schema.schema("getAccountInfoForOAuth.Response"))
                    .responseFields(
                        fieldWithPath("accountId").description("회원 시퀀스"),
                        fieldWithPath("loginId").description("회원 아이디"),
                        fieldWithPath("authority").description("회원 종류 (일반 회원, 사업자 회원)"),
                        fieldWithPath("status").description("회원 상태 (활성, 탈퇴, 휴면)")
                    )
                )
            );
    }


    @Test
    @DisplayName("회원 정보 조회 - 회원 시퀀스 기준으로 회원의 모든 정보 조회")
    void getAccount() throws Exception {
        SelectAccountResponseDto expectDto = new SelectAccountResponseDto(1L, "ACTIVE", "CUSTOMER",
            "마스터", "Test1", "테스트맨", "테스트좋아", "test@lover.dev",
            LocalDate.of(1998, 9, 20), "01012245221", LocalDateTime.now());

        when(accountService.selectAccount(expectDto.getId())).thenReturn(expectDto);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/{accountId}", expectDto.getId());

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getAccountInfoForOAuth",
                    ResourceSnippetParameters.builder()
                        .requestSchema(Schema.schema("getAccount.Request"))
                        .pathParameters(
                            parameterWithName("accountId").description("회원 시퀀스")
                        )
                        .responseSchema(Schema.schema("getAccount.Response"))
                        .responseFields(
                            fieldWithPath("id").description("Cookshoong 회원 시퀀스"),
                            fieldWithPath("status").description("회원 상태 (활성, 탈퇴, 휴면)"),
                            fieldWithPath("authority").description("회원 종류 (일반 회원, 사업자 회원)"),
                            fieldWithPath("rank").description("회원 등급"),
                            fieldWithPath("loginId").description("로그인 때 사용되는 id"),
                            fieldWithPath("name").description("이름"),
                            fieldWithPath("nickname").description("별명"),
                            fieldWithPath("email").description("이메일"),
                            fieldWithPath("birthday").description("생일"),
                            fieldWithPath("phoneNumber").description("핸드폰 번호"),
                            fieldWithPath("lastLoginAt").description("마지막 로그인 날짜")
                        )
                )
            );
    }

    @Test
    @DisplayName("회원 정보 조회 - 회원 시퀀스 기준으로 없는 회원을 조회하는 경우 404 에러 응답")
    void getAccount_2() throws Exception {
        when(accountService.selectAccount(anyLong())).thenThrow(UserNotFoundException.class);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/{accountId}", Long.MAX_VALUE);

        mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("아이디 존재여부 확인 - 존재하는 아이디로 조회")
    void getAccountExists() throws Exception {
        when(accountService.selectAccountExists(anyString())).thenReturn(HttpStatus.OK);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/login-id-exists/{loginId}", "유저1");

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getAccountExists",
                    ResourceSnippetParameters.builder()
                        .requestSchema(Schema.schema("getAccountExists.Request"))
                        .pathParameters(
                            parameterWithName("loginId").description("로그인 때 사용되는 id")
                        )
                )
            );
    }

    @Test
    @DisplayName("아이디 존재여부 확인 - 존재하지 않는 아이디로 조회")
    void getAccountExists_2() throws Exception {
        when(accountService.selectAccountExists(anyString())).thenReturn(HttpStatus.NOT_FOUND);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/login-id-exists/{loginId}", "Anonymous");

        mockMvc.perform(request)
            .andExpect(status().isNotFound());
    }
}
