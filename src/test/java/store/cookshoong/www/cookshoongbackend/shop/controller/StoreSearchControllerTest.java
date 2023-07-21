package store.cookshoong.www.cookshoongbackend.shop.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;

/**
 * StoreSearchController 에 대한 테스트 코드.
 *
 * @author jeongjewan
 * @since 2023.07.16
 */
@WebMvcTest(StoreSearchController.class)
@AutoConfigureRestDocs
class StoreSearchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private StoreService storeService;

    static SelectAllStoresNotOutedResponseDto selectAllStoresNotOutedResponseDto;

    @Test
    void getNotOutedStores() throws Exception {

        Long addressId = 8L;
        String storeCategoryCode = "CHK";
        Pageable pageable = Pageable.ofSize(10).withPage(1);

        List<SelectAllStoresNotOutedResponseDto> storeList = new ArrayList<>();
        storeList.add(new SelectAllStoresNotOutedResponseDto(
            1L, "네네치킨", "OPEN", "광주광역시 동구 필문대로287번길 19-24", "평양빌딩",
            new BigDecimal("35.1453447604175"), new BigDecimal("126.9292302170903"), "CHK",
            UUID.randomUUID()+".jpg"
        ));
        storeList.add(new SelectAllStoresNotOutedResponseDto(
            1L, "굽네치킨", "OPEN", "광주광역시 동구 필문대로273번길 8-5", "평양빌딩",
            new BigDecimal("35.1464529445461"), new BigDecimal("126.9283952407910"), "CHK",
            UUID.randomUUID()+".jpg"
        ));

        Page<SelectAllStoresNotOutedResponseDto> storePage = new PageImpl<>(storeList, pageable, storeList.size());

        when(storeService.selectAllStoresNotOutedResponsePage(
            eq(addressId),
            any(Pageable.class))).thenReturn(storePage);

        mockMvc.perform(get("/api/accounts/customer/{addressId}/stores", addressId)
            .param("storeCategoryCode", storeCategoryCode)
            .param("page", String.valueOf(pageable.getPageNumber()))
            .param("size", String.valueOf(pageable.getPageSize()))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-not-outed-stores",
                ResourceSnippetParameters.builder()
                    .requestSchema(schema("SelectAllStoresNotOutedResponse")),
                responseFields(
                    fieldWithPath("content").description("매장 목록 정보"),
                    fieldWithPath("content[].id").description("주소 아이디"),
                    fieldWithPath("content[].name").description("매장 이름"),
                    fieldWithPath("content[].storeStatus").description("매장 상태"),
                    fieldWithPath("content[].mainPlace").description("메인 주소"),
                    fieldWithPath("content[].detailPlace").description("상세 주소"),
                    fieldWithPath("content[].latitude").description("위도"),
                    fieldWithPath("content[].longitude").description("경도"),
                    fieldWithPath("content[].category").description("카테고리"),
                    fieldWithPath("content[].savedName").description("저장된이름"),
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
                ))
            );

        verify(storeService, times(1))
            .selectAllStoresNotOutedResponsePage(
            eq(addressId),
            any(Pageable.class)
            );
    }
}
