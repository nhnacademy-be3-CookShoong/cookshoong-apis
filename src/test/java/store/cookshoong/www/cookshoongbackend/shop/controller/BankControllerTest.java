package store.cookshoong.www.cookshoongbackend.shop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBankRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.BankTypeService;

/**
 * 관리자 : 은행 관리 시스템.
 *
 * @author seungyeon
 * @since 2023.08.11
 */
@AutoConfigureRestDocs
@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    BankTypeService bankTypeService;

    @Test
    @DisplayName("관리자 : 등록한 은행 페이지로 조회 - 성공")
    void getBanks() throws Exception {
        SelectAllBanksResponseDto bankType = new SelectAllBanksResponseDto("KB", "국민은행");
        SelectAllBanksResponseDto bankType2 = new SelectAllBanksResponseDto("DG", "대구은행");
        SelectAllBanksResponseDto bankType3 = new SelectAllBanksResponseDto("SHIN", "신한은행");
        SelectAllBanksResponseDto bankType4 = new SelectAllBanksResponseDto("HANA", "하나은행");
        List<SelectAllBanksResponseDto> banks = List.of(bankType, bankType2, bankType3, bankType4);

        when(bankTypeService.selectBanks(any(Pageable.class)))
            .thenAnswer(invocation -> new PageImpl<>(banks, invocation.getArgument(0), banks.size()));

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/admin/banks")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].bankTypeCode").value("KB"))
            .andExpect(jsonPath("$.content[0].description").value("국민은행"))
            .andExpect(jsonPath("$.content[1].bankTypeCode").value("DG"))
            .andExpect(jsonPath("$.content[1].description").value("대구은행"))
            .andExpect(jsonPath("$.content[2].bankTypeCode").value("SHIN"))
            .andExpect(jsonPath("$.content[2].description").value("신한은행"))
            .andExpect(jsonPath("$.content[3].bankTypeCode").value("HANA"))
            .andExpect(jsonPath("$.content[3].description").value("하나은행"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectBanks",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectBanks.Response")),
                responseFields(
                    fieldWithPath("content[].bankTypeCode").description("은행코드"),
                    fieldWithPath("content[].description").description("은행이름"),
                    fieldWithPath("pageable.sort.empty").description("정렬 데이터 공백 여부"),
                    fieldWithPath("pageable.sort.sorted").description("정렬 여부"),
                    fieldWithPath("pageable.sort.unsorted").description("비정렬 여부"),
                    fieldWithPath("pageable.offset").description("데이터 순번"),
                    fieldWithPath("pageable.pageNumber").description("페이지 번호"),
                    fieldWithPath("pageable.pageSize").description("한 페이지당 조회할 데이터 개수"),
                    fieldWithPath("pageable.paged").description("페이징 정보 포함 여부"),
                    fieldWithPath("pageable.unpaged").description("페이징 정보 미포함 여부"),
                    fieldWithPath("last").description("마지막 페이지 여부"),
                    fieldWithPath("totalPages").description("전체 페이지 개수"),
                    fieldWithPath("totalElements").description("총 데이터 개수"),
                    fieldWithPath("first").description("첫 페이지 여부"),
                    fieldWithPath("size").description("한 페이지당 조회할 데이터 개수"),
                    fieldWithPath("number").description("현재 페이지 번호"),
                    fieldWithPath("sort.empty").description("정렬 데이터 공백 여부"),
                    fieldWithPath("sort.sorted").description("정렬 여부"),
                    fieldWithPath("sort.unsorted").description("비정렬 여부"),
                    fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
                    fieldWithPath("empty").description("데이터 미존재 여부")
                )
            ));
    }

    @Test
    @DisplayName("관리자 : 은행 등록")
    void postBank() throws Exception {
        CreateBankRequestDto requestDto = ReflectionUtils.newInstance(CreateBankRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "bankCode", "IBK");
        ReflectionTestUtils.setField(requestDto, "bankName", "IBK기업은행");

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .post("/api/admin/banks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createBanks",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createBanks.Request")),
                requestFields(fieldWithPath("bankCode").description("은행코드"),
                    fieldWithPath("bankName").description("은행이름"))
            ));

        verify(bankTypeService, times(1)).createBank(any(CreateBankRequestDto.class));
    }
}
