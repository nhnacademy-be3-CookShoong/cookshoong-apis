package store.cookshoong.www.cookshoongbackend.account.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.BankTypeService;
import store.cookshoong.www.cookshoongbackend.shop.service.MerchantService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreStatusService;

/**
 * 사업자 페이지에서 필요한 은행, 카테고리, 상태 코드 리스트 조회.
 *
 * @author seungyeon
 * @since 2023.08.11
 */
@AutoConfigureRestDocs
@WebMvcTest(BusinessSelectController.class)
class BusinessSelectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    BankTypeService bankTypeService;

    @MockBean
    MerchantService merchantService;
    @MockBean
    StoreStatusService storeStatusService;

    @Test
    @DisplayName("은행 리스트 조회")
    void getBanksForUser() throws Exception {
        SelectAllBanksResponseDto bankType = new SelectAllBanksResponseDto("KB", "국민은행");
        SelectAllBanksResponseDto bankType2 = new SelectAllBanksResponseDto("DG", "대구은행");
        SelectAllBanksResponseDto bankType3 = new SelectAllBanksResponseDto("SHIN", "신한은행");
        SelectAllBanksResponseDto bankType4 = new SelectAllBanksResponseDto("HANA", "하나은행");
        List<SelectAllBanksResponseDto> banks = List.of(bankType, bankType2, bankType3, bankType4);

        when(bankTypeService.selectBanksForUser()).thenReturn(banks);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/accounts/banks")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].bankTypeCode").value("KB"))
            .andExpect(jsonPath("$.[0].description").value("국민은행"))
            .andExpect(jsonPath("$.[1].bankTypeCode").value("DG"))
            .andExpect(jsonPath("$.[1].description").value("대구은행"))
            .andExpect(jsonPath("$.[2].bankTypeCode").value("SHIN"))
            .andExpect(jsonPath("$.[2].description").value("신한은행"))
            .andExpect(jsonPath("$.[3].bankTypeCode").value("HANA"))
            .andExpect(jsonPath("$.[3].description").value("하나은행"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectBanksForUser",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectBanksForUser.Response")),
                responseFields(
                    fieldWithPath("[].bankTypeCode").description("은행코드"),
                    fieldWithPath("[].description").description("은행이름")
                )
            ));
        verify(bankTypeService, times(1)).selectBanksForUser();
    }

    @Test
    @DisplayName("가맹점 리스트 조회")
    void getMerchantsForUser() throws Exception {
        SelectAllMerchantsResponseDto merchant = new SelectAllMerchantsResponseDto(1L, "네네치킨");
        SelectAllMerchantsResponseDto merchant2 = new SelectAllMerchantsResponseDto(2L, "비비큐");
        SelectAllMerchantsResponseDto merchant3 = new SelectAllMerchantsResponseDto(3L, "원할머니보쌈");

        List<SelectAllMerchantsResponseDto> merchants = List.of(merchant, merchant2, merchant3);

        when(merchantService.selectAllMerchantsForUser()).thenReturn(merchants);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/accounts/merchants")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(1))
            .andExpect(jsonPath("$.[0].name").value("네네치킨"))
            .andExpect(jsonPath("$.[1].id").value(2))
            .andExpect(jsonPath("$.[1].name").value("비비큐"))
            .andExpect(jsonPath("$.[2].id").value(3))
            .andExpect(jsonPath("$.[2].name").value("원할머니보쌈"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectMerchantsForUser",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectMerchantsForUser.Response")),
                responseFields(
                    fieldWithPath("[].id").description("가맹점 아이디"),
                    fieldWithPath("[].name").description("가맹점명")
                )
            ));
    }

    @Test
    @DisplayName("매장 상태 조회")
    void getStoreStatusForUser() throws Exception {
        SelectAllStatusResponseDto status1 = new SelectAllStatusResponseDto(StoreStatus.StoreStatusCode.CLOSE.name(), "준비중");
        SelectAllStatusResponseDto status2 = new SelectAllStatusResponseDto(StoreStatus.StoreStatusCode.OPEN.name(), "영업중");
        SelectAllStatusResponseDto status3 = new SelectAllStatusResponseDto(StoreStatus.StoreStatusCode.OUTED.name(), "운영중지");

        List<SelectAllStatusResponseDto> merchants = List.of(status1, status2, status3);

        when(storeStatusService.selectAllStatusForUser()).thenReturn(merchants);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/accounts/store-status")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].code").value("CLOSE"))
            .andExpect(jsonPath("$.[0].description").value("준비중"))
            .andExpect(jsonPath("$.[1].code").value("OPEN"))
            .andExpect(jsonPath("$.[1].description").value("영업중"))
            .andExpect(jsonPath("$.[2].code").value("OUTED"))
            .andExpect(jsonPath("$.[2].description").value("운영중지"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectStatusForStore",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectStatusForStore.Response")),
                responseFields(
                    fieldWithPath("[].code").description("상태코드"),
                    fieldWithPath("[].description").description("상태설명")
                )
            ));
    }
}
