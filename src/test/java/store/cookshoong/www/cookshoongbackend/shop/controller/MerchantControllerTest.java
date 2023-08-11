package store.cookshoong.www.cookshoongbackend.shop.controller;

import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.MerchantService;

/**
 * 관리자 : 가맹점 관리 시스템 컨트롤러.
 *
 * @author seungyeon
 * @since 2023.08.11
 */
@AutoConfigureRestDocs
@WebMvcTest(MerchantController.class)
class MerchantControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MerchantService merchantService;

    @Test
    void getMerchants() throws Exception {
        SelectAllMerchantsResponseDto merchant = new SelectAllMerchantsResponseDto(1L, "네네치킨");
        SelectAllMerchantsResponseDto merchant2 = new SelectAllMerchantsResponseDto(2L, "비비큐");
        SelectAllMerchantsResponseDto merchant3 = new SelectAllMerchantsResponseDto(3L, "원할머니보쌈");

        List<SelectAllMerchantsResponseDto> merchants = List.of(merchant, merchant2, merchant3);

        when(merchantService.selectAllMerchants(any(Pageable.class)))
            .thenAnswer(invocation -> new PageImpl<>(merchants, invocation.getArgument(0), merchants.size()));

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/admin/merchants")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("네네치킨"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].name").value("비비큐"))
            .andExpect(jsonPath("$.content[2].id").value(3))
            .andExpect(jsonPath("$.content[2].name").value("원할머니보쌈"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectMerchants",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectMerchants.Response")),
                responseFields(
                    fieldWithPath("content[].id").description("가맹점 아이디"),
                    fieldWithPath("content[].name").description("가맹점명"),
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
    void postMerchant() throws Exception {
        CreateMerchantRequestDto requestDto = ReflectionUtils.newInstance(CreateMerchantRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "merchantName", "도미노피자");

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .post("/api/admin/merchants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto));
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createMerchant",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createMerchant.Request")),
                requestFields(fieldWithPath("merchantName").description("가맹점명"))
            ));
    }

    @Test
    void patchMerchant() throws Exception {
        UpdateMerchantRequestDto requestDto = ReflectionUtils.newInstance(UpdateMerchantRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "merchantName", "BHC치킨");

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .patch("/api/admin/merchants/{merchantId}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("updateMerchant",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("merchantName").description("가맹점명"))
                    .requestSchema(schema("updateMerchant.Request")),
                requestFields(
                    fieldWithPath("merchantName").description("가맹점명")
                )));
    }

    @Test
    void deleteStore() throws Exception {
        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .delete("/api/admin/merchants/{merchantId}", 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(MockMvcRestDocumentationWrapper.document("removeMerchant",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("merchantId").description("가맹점 번호"))
                    .requestSchema(schema("removeMerchant.Request"))));
    }
}
