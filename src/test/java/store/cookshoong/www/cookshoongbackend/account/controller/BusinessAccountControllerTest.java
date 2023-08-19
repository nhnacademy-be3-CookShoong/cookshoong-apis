package store.cookshoong.www.cookshoongbackend.account.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Ref;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreCategoriesDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;

/**
 * 사업자 : 매장 조회, 등록, 수정 등을 다루는 컨트롤러.
 *
 * @author seungyeon
 * @since 2023.08.11
 */
@AutoConfigureRestDocs
@WebMvcTest(BusinessAccountController.class)
class BusinessAccountControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    StoreService storeService;
    @Test
    @DisplayName("사장님이 소유한 가게 리스트 확인하기")
    void getStores() throws Exception {
        SelectAllStoresResponseDto storesResponseDto =
            new SelectAllStoresResponseDto(1L, "두찜 조선대점",
                "광주광역시 동구 지산동 2길 17", "20-1", "영업중");
        SelectAllStoresResponseDto storesResponseDto2 =
            new SelectAllStoresResponseDto(2L, "피자스쿨 조선대점",
                "광주광역시 동구 용산동 3길 21", "11-9", "휴식중");
        SelectAllStoresResponseDto storesResponseDto3 =
            new SelectAllStoresResponseDto(3L, "파스쿠찌 조선대점",
                "광주광역시 동구 동명동 8길 11", "1층 상가", "영업중");

        List<SelectAllStoresResponseDto> stores = List.of(storesResponseDto, storesResponseDto2, storesResponseDto3);

        when(storeService.selectAllStores(anyLong())).thenReturn(stores);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/accounts/{accountId}/stores", 1L)
            .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].storeId").value(1))
            .andExpect(jsonPath("$.[0].storeName").value("두찜 조선대점"))
            .andExpect(jsonPath("$.[0].storeMainAddress").value("광주광역시 동구 지산동 2길 17"))
            .andExpect(jsonPath("$.[0].storeDetailAddress").value("20-1"))
            .andExpect(jsonPath("$.[0].storeStatus").value("영업중"))
            .andExpect(jsonPath("$.[1].storeId").value(2))
            .andExpect(jsonPath("$.[1].storeName").value("피자스쿨 조선대점"))
            .andExpect(jsonPath("$.[1].storeMainAddress").value("광주광역시 동구 용산동 3길 21"))
            .andExpect(jsonPath("$.[1].storeDetailAddress").value("11-9"))
            .andExpect(jsonPath("$.[1].storeStatus").value("휴식중"))
            .andExpect(jsonPath("$.[2].storeId").value(3))
            .andExpect(jsonPath("$.[2].storeName").value("파스쿠찌 조선대점"))
            .andExpect(jsonPath("$.[2].storeMainAddress").value("광주광역시 동구 동명동 8길 11"))
            .andExpect(jsonPath("$.[2].storeDetailAddress").value("1층 상가"))
            .andExpect(jsonPath("$.[2].storeStatus").value("영업중"))
            .andDo(MockMvcRestDocumentationWrapper.document("storeOwnedByBusiness",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("storeOwnedByBusiness.Response")),
                responseFields(
                    fieldWithPath("[].storeId").description("매장아이디"),
                    fieldWithPath("[].storeName").description("매장이름"),
                    fieldWithPath("[].storeMainAddress").description("메인주소"),
                    fieldWithPath("[].storeDetailAddress").description("상세주소"),
                    fieldWithPath("[].storeStatus").description("매장상태")
                )
            ));
    }

    @Test
    @DisplayName("사업자 : 해당 가게 정보 확인")
    void getStoreForUser() throws Exception {
        SelectStoreCategoriesDto categoriesDto = new SelectStoreCategoriesDto("한식");
        SelectStoreCategoriesDto categoriesDto2 = new SelectStoreCategoriesDto("야식");
        List<SelectStoreCategoriesDto> storeCategoriesDtos = List.of(categoriesDto, categoriesDto2);
        SelectStoreResponseDto responseDto = new SelectStoreResponseDto("1107098621", "김국밥",
            LocalDate.of(2023,8,1), "우리의 국밥집", "06265456178",
            "광주 동구 조선대길 7","24", new BigDecimal("35.1443748245301"), new BigDecimal("126.9274342347551"),
            new BigDecimal("9.9"), 10000,"<p>가게 설명을 입력해주세요.</p>", "국민은행",
            "111111111111", "http://objectstorage.storeImage.png",
            "objectStorage", "storeImage",  5000, "영업중");
        responseDto.setStoreCategories(storeCategoriesDtos);

        when(storeService.selectStore(anyLong(), anyLong())).thenReturn(responseDto);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/accounts/{accountId}/stores/{storeId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessLicenseNumber").value("1107098621"))
            .andExpect(jsonPath("$.representativeName").value("김국밥"))
            .andExpect(jsonPath("$.openingDate").value("2023-08-01"))
            .andExpect(jsonPath("$.storeName").value("우리의 국밥집"))
            .andExpect(jsonPath("$.phoneNumber").value("06265456178"))
            .andExpect(jsonPath("$.mainPlace").value("광주 동구 조선대길 7"))
            .andExpect(jsonPath("$.detailPlace").value("24"))
            .andExpect(jsonPath("$.latitude").value("35.1443748245301"))
            .andExpect(jsonPath("$.longitude").value("126.9274342347551"))
            .andExpect(jsonPath("$.defaultEarningRate").value("9.9"))
            .andExpect(jsonPath("$.description").value("<p>가게 설명을 입력해주세요.</p>"))
            .andExpect(jsonPath("$.bankCode").value("국민은행"))
            .andExpect(jsonPath("$.bankAccountNumber").value("111111111111"))
            .andExpect(jsonPath("$.pathName").value("http://objectstorage.storeImage.png"))
            .andExpect(jsonPath("$.locationType").value("objectStorage"))
            .andExpect(jsonPath("$.domainName").value("storeImage"))
            .andExpect(jsonPath("$.minimumOrderPrice").value(10000))
            .andExpect(jsonPath("$.deliveryCost").value(5000))
            .andExpect(jsonPath("$.storeStatus").value("영업중"))
            .andExpect(jsonPath("$.storeCategories[0].storeCategory").value("한식"))
            .andExpect(jsonPath("$.storeCategories[1].storeCategory").value("야식"))
            .andDo(MockMvcRestDocumentationWrapper.document("StoreInformationForBusiness",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("storeInformationForBusiness.Response")),
                responseFields(
                    fieldWithPath("businessLicenseNumber").description("사업자번호"),
                    fieldWithPath("representativeName").description("사업자이름"),
                    fieldWithPath("openingDate").description("개업일자"),
                    fieldWithPath("storeName").description("매장이름"),
                    fieldWithPath("phoneNumber").description("대표번호"),
                    fieldWithPath("mainPlace").description("매장 메인주소"),
                    fieldWithPath("detailPlace").description("매장 상세주소"),
                    fieldWithPath("latitude").description("위도"),
                    fieldWithPath("longitude").description("경도"),
                    fieldWithPath("defaultEarningRate").description("기본적립률"),
                    fieldWithPath("description").description("설명"),
                    fieldWithPath("bankCode").description("정산은행명"),
                    fieldWithPath("bankAccountNumber").description("정산계좌번호"),
                    fieldWithPath("pathName").description("매장 이미지 경로"),
                    fieldWithPath("locationType").description("사진 저장소"),
                    fieldWithPath("domainName").description("사진 저장폴더"),
                    fieldWithPath("minimumOrderPrice").description("최소주문금액"),
                    fieldWithPath("deliveryCost").description("배달비"),
                    fieldWithPath("storeStatus").description("매장상태"),
                    fieldWithPath("storeCategories[].storeCategory").description("매장 카테고리")
                )
            ));
    }

}
