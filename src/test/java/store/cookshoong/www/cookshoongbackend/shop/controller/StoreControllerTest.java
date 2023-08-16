package store.cookshoong.www.cookshoongbackend.shop.controller;

import static org.mockito.ArgumentMatchers.anyLong;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableCouponPolicyResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponPolicyService;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreCategoryService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * 사용자 : 매장 정보 조회.
 *
 * @author seungyeon
 * @since 2023.08.11
 */
@AutoConfigureRestDocs
@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;
    TestEntity testEntity = new TestEntity();

    @MockBean
    StoreService storeService;

    @MockBean
    StoreCategoryService storeCategoryService;

    @MockBean
    CouponPolicyService couponPolicyService;

    @Test
    @DisplayName("사용자 : 매장에 대한 정보 조회")
    void getStoreInformation() throws Exception {

        SelectProvableCouponPolicyResponseDto storeCoupon = new SelectProvableCouponPolicyResponseDto(1L, testEntity.getCouponTypeCash_1000_10000(), 1);
        SelectProvableCouponPolicyResponseDto storeCoupon2 = new SelectProvableCouponPolicyResponseDto(2L, testEntity.getCouponTypePercent_3_1000_10000(), 2);

        List<SelectProvableCouponPolicyResponseDto> couponPolicyResponseDtos = List.of(storeCoupon, storeCoupon2);

        SelectStoreForUserResponseDto responseDto =
            new SelectStoreForUserResponseDto("8910290128", "김사장",
                LocalDate.parse("2021-02-10"), "도미노피자", "05099043920", "광주광역시 동구 용산동",
                "3길 17", "피자 맛집 도미노피자입니다^^", "objectStorage", FileDomain.STORE_IMAGE.getVariable(),
                UUID.randomUUID() + ".png", 0, 4000, StoreStatus.StoreStatusCode.OPEN.name());

        when(storeService.selectStoreForUser(anyLong(), anyLong())).thenReturn(responseDto);
        when(couponPolicyService.getProvableStoreCouponPolicies(anyLong())).thenReturn(couponPolicyResponseDtos);
        responseDto.setDistance(5000);
        responseDto.setDeliveryTime(15);
        responseDto.setTotalDeliveryCost(5000);
        responseDto.setDistance(780);
        responseDto.setProvableCouponPolicies(couponPolicyResponseDtos);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/info", 1L)
            .param("addressId", "1")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.provableCouponPolicies[0].couponPolicyId").value(1))
            .andExpect(jsonPath("$.provableCouponPolicies[0].couponTypeResponse.type").value("cash"))
            .andExpect(jsonPath("$.provableCouponPolicies[0].couponTypeResponse.discountAmount").value(1000))
            .andExpect(jsonPath("$.provableCouponPolicies[0].couponTypeResponse.minimumOrderPrice").value(10000))
            .andExpect(jsonPath("$.provableCouponPolicies[0].usagePeriod").value(1))
            .andExpect(jsonPath("$.provableCouponPolicies[1].couponPolicyId").value(2))
            .andExpect(jsonPath("$.provableCouponPolicies[1].couponTypeResponse.type").value("percent"))
            .andExpect(jsonPath("$.provableCouponPolicies[1].couponTypeResponse.rate").value(3))
            .andExpect(jsonPath("$.provableCouponPolicies[1].couponTypeResponse.maximumDiscountAmount").value(1000))
            .andExpect(jsonPath("$.provableCouponPolicies[1].couponTypeResponse.minimumOrderPrice").value(10000))
            .andExpect(jsonPath("$.provableCouponPolicies[1].usagePeriod").value(2))
            .andDo(MockMvcRestDocumentationWrapper.document("selectStoreInformation",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectStoreInformation.Response")),
                responseFields(
                    fieldWithPath("businessLicenseNumber").description("사업자등록번호"),
                    fieldWithPath("representativeName").description("사업자이름"),
                    fieldWithPath("openingDate").description("개업일자"),
                    fieldWithPath("storeName").description("가게명"),
                    fieldWithPath("phoneNumber").description("전화번호"),
                    fieldWithPath("mainPlace").description("메인주소"),
                    fieldWithPath("detailPlace").description("상세주소"),
                    fieldWithPath("description").description("가게 소개"),
                    fieldWithPath("minimumOrderPrice").description("최소금액"),
                    fieldWithPath("deliveryCost").description("배달비"),
                    fieldWithPath("locationType").description("저장소타입"),
                    fieldWithPath("domainName").description("폴더명"),
                    fieldWithPath("savedName").description("전체경로"),
                    fieldWithPath("provableCouponPolicies[].couponPolicyId").description("정책 id"),
                    fieldWithPath("provableCouponPolicies[].couponTypeResponse.type").description("쿠폰타입 설명"),
                    fieldWithPath("provableCouponPolicies[].couponTypeResponse.rate").optional().description("할인율"),
                    fieldWithPath("provableCouponPolicies[].couponTypeResponse.discountAmount").optional().description("할인금액"),
                    fieldWithPath("provableCouponPolicies[].couponTypeResponse.maximumDiscountAmount").optional().description("할인금액"),
                    fieldWithPath("provableCouponPolicies[].couponTypeResponse.minimumOrderPrice").description("최소주문금액"),
                    fieldWithPath("provableCouponPolicies[].usagePeriod").description("발급 후 사용기간"),
                    fieldWithPath("storeStatus").description("매장상태"),
                    fieldWithPath("distance").description("사용자와의 거리"),
                    fieldWithPath("totalDeliveryCost").description("총 배달비"),
                    fieldWithPath("deliveryTime").description("배달 시간")
                )));
        verify(storeService, times(1)).selectStoreForUser(anyLong(), anyLong());
        verify(couponPolicyService, times(1)).getProvableStoreCouponPolicies(anyLong());
    }

    @Test
    @DisplayName("카테고리 리스트 조회")
    void getStoreCategories() throws Exception {
        SelectAllCategoriesResponseDto storeCategory = new SelectAllCategoriesResponseDto("BOX", "도시락");
        SelectAllCategoriesResponseDto storeCategory2 = new SelectAllCategoriesResponseDto("CHK", "치킨");

        List<SelectAllCategoriesResponseDto> categories = List.of(storeCategory, storeCategory2);

        when(storeCategoryService.selectAllCategoriesForUser()).thenReturn(categories);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/categories")
            .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].categoryCode").value("BOX"))
            .andExpect(jsonPath("$.[0].description").value("도시락"))
            .andExpect(jsonPath("$.[1].categoryCode").value("CHK"))
            .andExpect(jsonPath("$.[1].description").value("치킨"))
            .andDo(MockMvcRestDocumentationWrapper.document("selectCategories",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectCategories.Response")),
                responseFields(
                    fieldWithPath("[].categoryCode").description("카테고리 코드"),
                    fieldWithPath("[].description").description("카테고리명")
                )
            ));
    }
}
