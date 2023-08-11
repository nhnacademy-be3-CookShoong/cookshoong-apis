package store.cookshoong.www.cookshoongbackend.shop.controller;

import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreCategoryService;

/**
 * 관리자 : 카테고리 관리 시스템.
 *
 * @author seungyeon
 * @since 2023.08.11
 */
@AutoConfigureRestDocs
@WebMvcTest(StoreCategoryController.class)
class StoreCategoryControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StoreCategoryService storeCategoryService;

    @Test
    @DisplayName("관리자 : 카테고리 조회")
    void getStoreCategories() throws Exception {
        SelectAllCategoriesResponseDto storeCategory = new SelectAllCategoriesResponseDto("BOX", "도시락");
        SelectAllCategoriesResponseDto storeCategory2 = new SelectAllCategoriesResponseDto("CHK", "치킨");
        SelectAllCategoriesResponseDto storeCategory3 = new SelectAllCategoriesResponseDto("CHN", "중식");
        SelectAllCategoriesResponseDto storeCategory4 = new SelectAllCategoriesResponseDto("PIZ", "피자");

        List<SelectAllCategoriesResponseDto> categories = List.of(storeCategory, storeCategory2, storeCategory3, storeCategory4);

        when(storeCategoryService.selectAllCategories(any(Pageable.class)))
            .thenAnswer(invocation -> new PageImpl<>(categories, invocation.getArgument(0), categories.size()));

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/admin/categories")
            .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].categoryCode").value("BOX"))
            .andExpect(jsonPath("$.content[0].description").value("도시락"))
            .andExpect(jsonPath("$.content[1].categoryCode").value("CHK"))
            .andExpect(jsonPath("$.content[1].description").value("치킨"))
            .andExpect(jsonPath("$.content[2].categoryCode").value("CHN"))
            .andExpect(jsonPath("$.content[2].description").value("중식"))
            .andExpect(jsonPath("$.content[3].categoryCode").value("PIZ"))
            .andExpect(jsonPath("$.content[3].description").value("피자"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectStoreCategories",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectStoreCategories.Response")),
                responseFields(
                    fieldWithPath("content[].categoryCode").description("카테고리 코드"),
                    fieldWithPath("content[].description").description("카테고리명"),
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
    @DisplayName("관리자 : 카테고리 등록")
    void postStoreCategory() throws Exception {
        CreateStoreCategoryRequestDto requestDto = ReflectionUtils.newInstance(CreateStoreCategoryRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "storeCategoryCode", "CHK");
        ReflectionTestUtils.setField(requestDto, "storeCategoryName", "치킨");

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .post("/api/admin/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createStoreCategory",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createStoreCategory.Request")),
                requestFields(fieldWithPath("storeCategoryCode").description("카테고리 코드"),
                    fieldWithPath("storeCategoryName").description("카테고리명"))
            ));

        verify(storeCategoryService, times(1)).createStoreCategory(any(CreateStoreCategoryRequestDto.class));
    }

    @Test
    @DisplayName("관리자 : 카테고리 수정")
    void patchStoreCategory() throws Exception {
        UpdateStoreCategoryRequestDto requestDto = ReflectionUtils.newInstance(UpdateStoreCategoryRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "storeCategoryName", "중식");

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .patch("/api/admin/categories/{categoryCode}", "CHK")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("updateStoreCategory",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("categoryCode").description("카테고리 코드"))
                    .requestSchema(schema("updateStoreCategory.Request")),
                requestFields(
                    fieldWithPath("storeCategoryName").description("카테고리 이름")
                )));
    }

    @Test
    @DisplayName("관리자 : 카테고리 삭제")
    void deleteStoreCategory() throws Exception {
        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .delete("/api/admin/categories/{categoryCode}", "CHK")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(MockMvcRestDocumentationWrapper.document("removeStoreCategory",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("categoryCode").description("카테고리 코드"))
                    .requestSchema(schema("removeStoreCategory.Request"))));
    }
}
