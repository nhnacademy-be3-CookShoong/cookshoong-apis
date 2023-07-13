package store.cookshoong.www.cookshoongbackend.address.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
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
                    .requestSchema(schema("ModifyAccountAddressRequest")),
                requestFields(
                    fieldWithPath("detailPlace").description("변경된 상세 주소")
                )));
    }

    @Test
    @DisplayName("회원이 가지고 있느 모든 메인 주소와 별칭 조회")
    void getAccountAddressList() throws Exception {

        AccountAddressResponseDto responseDto =
            new AccountAddressResponseDto(1L, "NHN", "경기도 성남시 분당구 대왕판교로645번길 16");

        List<AccountAddressResponseDto> accountAddresses = Collections.singletonList(responseDto);

        when(addressService.selectAccountAddressList(1L)).thenReturn(accountAddresses);

        mockMvc.perform(get("/api/addresses/{accountId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(responseDto.getId()))
            .andExpect(jsonPath("$[0].alias").value(responseDto.getAlias()))
            .andExpect(jsonPath("$[0].mainPlace").value(responseDto.getMainPlace()))
            .andDo(document("get-account-address-list",
                responseFields(
                    fieldWithPath("[].id").description("주소 아이디"),
                    fieldWithPath("[].alias").description("별칭"),
                    fieldWithPath("[].mainPlace").description("메인 주소")
                )));
    }

    @Test
    @DisplayName("회원이 주문할 때 보여줄 최근 등록 메인 주소와 상세 주소, 회원이 최근의 등록한 주소의 위치를 보여줄 좌표")
    void getAccountAddressForPayment() throws Exception {

        AddressResponseDto responseDto = new AddressResponseDto("경기도 성남시 분당구 대왕판교로645번길 16", "NHN 플레이뮤지엄",
            new BigDecimal("37.40096549041187"), new BigDecimal("127.1040493631922"));

        when(addressService.selectAccountAddressRecentRegistration(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/addresses/{accountId}/recent-registration",  1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mainPlace").value(responseDto.getMainPlace()))
            .andExpect(jsonPath("$.detailPlace").value(responseDto.getDetailPlace()))
            .andExpect(jsonPath("$.latitude").value(responseDto.getLatitude()))
            .andExpect(jsonPath("$.longitude").value(responseDto.getLongitude()))
            .andDo(document("get-account-address",
                responseFields(
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
            .andDo(document("delete-account-address"));
    }
}
