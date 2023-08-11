package store.cookshoong.www.cookshoongbackend.address.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.address.model.request.CreateAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.model.request.ModifyAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;

/**
 * 주소에 대한 Controller 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.13
 */
@AutoConfigureRestDocs
@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;
    static CreateAccountAddressRequestDto createAccountAddressRequestDto;

    @BeforeEach
    void setup() {

        createAccountAddressRequestDto = ReflectionUtils.newInstance(CreateAccountAddressRequestDto.class);

        ReflectionTestUtils.setField(createAccountAddressRequestDto, "alias", "NHN");
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "mainPlace", "경기도 성남시 분당구 대왕판교로645번길 16");
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "detailPlace", "NHN 플레이뮤지엄");
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "latitude", new BigDecimal("37.40096549041187"));
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "longitude", new BigDecimal("127.1040493631922"));
    }

    @Test
    @DisplayName("회원이 주소를 등록")
    void postCreateAccountAddress() throws Exception {

        mockMvc.perform(post("/api/addresses/{accountId}", 1L)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createAccountAddressRequestDto)))
            .andExpect(status().isCreated())
            .andDo(document("post-create-account-address",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .requestSchema(schema("CreateAccountAddressRequest")),
                requestFields(
                    fieldWithPath("alias").description("별칭"),
                    fieldWithPath("mainPlace").description("메인 주소"),
                    fieldWithPath("detailPlace").description("상세 주소"),
                    fieldWithPath("latitude").description("위도"),
                    fieldWithPath("longitude").description("경도")
                )));
    }

    @Test
    @DisplayName("회원이 주소를 등록 - 유효성 검사 실패, 빈 값이 들어올 때")
    void postCreateAccountAddress_ValidationFailed() throws Exception {
        ReflectionTestUtils.setField(createAccountAddressRequestDto, "mainPlace", null);
        // 유효성 검사에 실패하는 필드를 설정

        mockMvc.perform(post("/api/addresses/{accountId}", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountAddressRequestDto)))
            .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("회원의 상세 주소를 변경")
    void patchModifyAccountDetailAddress() throws Exception {
        ModifyAccountAddressRequestDto requestDto = ReflectionUtils.newInstance(ModifyAccountAddressRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "detailPlace", "NHN Payco");

        mockMvc.perform(patch("/api/addresses/{accountId}/{addressId}", 1L, 1L)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(document("patch-modify-account-detail-address",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .pathParameters(parameterWithName("addressId").description("주소 아이디"))
                    .requestSchema(schema("ModifyAccountAddressRequest")),
                requestFields(
                    fieldWithPath("detailPlace").description("변경된 상세 주소")
                )));
    }

    @Test
    @DisplayName("회원의 상세 주소를 변경 - 유효성 검사 실패, 옮지 않으 값이 들어갈 때")
    void patchModifyAccountDetailAddress_ValidationFailed() throws Exception {
        ModifyAccountAddressRequestDto requestDto = ReflectionUtils.newInstance(ModifyAccountAddressRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "detailPlace", "--???--");

        mockMvc.perform(patch("/api/addresses/{accountId}/{addressId}", 1L, 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원이 선택한 주소에 대해 갱신 날짜를 업데이트")
    void patchSelectAccountAddressRenewalAt() throws Exception {

        mockMvc.perform(patch("/api/addresses/{accountId}/select/{addressId}", 1L, 1L))
            .andExpect(status().isOk())
            .andDo(document("patch-select-account-address-renewal-at",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .pathParameters(parameterWithName("addressId").description("주소 아이디"))
                    .responseSchema(Schema.schema("getStorePolicy.Response"))));
    }

    @Test
    @DisplayName("회원이 가지고 있느 모든 메인 주소와 별칭 조회")
    void getAccountAddressList() throws Exception {

        AccountAddressResponseDto responseDto =
            new AccountAddressResponseDto(1L, "NHN", "경기도 성남시 분당구 대왕판교로645번길 16", "1층 삼거리");

        List<AccountAddressResponseDto> accountAddresses = Collections.singletonList(responseDto);

        when(addressService.selectAccountAddressList(1L)).thenReturn(accountAddresses);

        mockMvc.perform(get("/api/addresses/{accountId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(responseDto.getId()))
            .andExpect(jsonPath("$[0].alias").value(responseDto.getAlias()))
            .andExpect(jsonPath("$[0].mainPlace").value(responseDto.getMainPlace()))
            .andExpect(jsonPath("$[0].detailPlace").value(responseDto.getDetailPlace()))
            .andDo(document("get-account-address-list",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .requestSchema(schema("AccountAddressList")),
                responseFields(
                    fieldWithPath("[].id").description("주소 아이디"),
                    fieldWithPath("[].alias").description("별칭"),
                    fieldWithPath("[].mainPlace").description("메인 주소"),
                    fieldWithPath("[].detailPlace").description("메인 주소")
                )));
    }

    @Test
    @DisplayName("회원이 최근에 갱신한 주소와 좌표 조회")
    void getAccountAddressRenewalAt() throws Exception {

        AddressResponseDto responseDto = new AddressResponseDto(1L, "경기도 성남시 분당구 대왕판교로645번길 16", "NHN 플레이뮤지엄",
            new BigDecimal("37.40096549041187"), new BigDecimal("127.1040493631922"));

        when(addressService.selectAccountAddressRenewalAt(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/addresses/{accountId}/renewal-at",  1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mainPlace").value(responseDto.getMainPlace()))
            .andExpect(jsonPath("$.detailPlace").value(responseDto.getDetailPlace()))
            .andExpect(jsonPath("$.latitude").value(responseDto.getLatitude()))
            .andExpect(jsonPath("$.longitude").value(responseDto.getLongitude()))
            .andDo(document("get-account-address",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .requestSchema(schema("AccountAddressRenewalAt")),
                responseFields(
                    fieldWithPath("id").description("주소 아아디"),
                    fieldWithPath("mainPlace").description("메인 주소"),
                    fieldWithPath("detailPlace").description("상세 주소"),
                    fieldWithPath("latitude").description("위도"),
                    fieldWithPath("longitude").description("경도")
                )));
    }

    @Test
    @DisplayName("회원이 주소록에 선택한 주소 정보")
    void getAccountChoiceAddress() throws Exception {

        AddressResponseDto responseDto = new AddressResponseDto(1L, "경기도 성남시 분당구 대왕판교로645번길 16", "NHN 플레이뮤지엄",
            new BigDecimal("37.40096549041187"), new BigDecimal("127.1040493631922"));

        when(addressService.selectAccountChoiceAddress(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/addresses/{addressId}/choice",  1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mainPlace").value(responseDto.getMainPlace()))
            .andExpect(jsonPath("$.detailPlace").value(responseDto.getDetailPlace()))
            .andExpect(jsonPath("$.latitude").value(responseDto.getLatitude()))
            .andExpect(jsonPath("$.longitude").value(responseDto.getLongitude()))
            .andDo(document("get-account-address",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .requestSchema(schema("AccountChoiceAddress")),
                responseFields(
                    fieldWithPath("id").description("주소 아이디"),
                    fieldWithPath("mainPlace").description("메인 주소"),
                    fieldWithPath("detailPlace").description("상세 주소"),
                    fieldWithPath("latitude").description("위도"),
                    fieldWithPath("longitude").description("경도")
                )));
    }

    @Test
    @DisplayName("회원이 지정한 특정 주소를 삭제")
    void deleteAccountAddress() throws Exception {

        mockMvc.perform(delete("/api/addresses/{accountId}/{addressId}", 1L, 1L))
            .andExpect(status().isNoContent())
            .andDo(document("delete-account-address",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("회원 아이디"))
                    .pathParameters(parameterWithName("addressId").description("주소 아이디"))
                    .requestSchema(schema("DeleteAccountAddress"))));
    }
}
