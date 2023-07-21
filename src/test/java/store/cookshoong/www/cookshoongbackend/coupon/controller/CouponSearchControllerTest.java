package store.cookshoong.www.cookshoongbackend.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectOwnCouponResponseDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypeCashVo;
import store.cookshoong.www.cookshoongbackend.coupon.model.vo.CouponTypePercentVo;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponSearchService;

@AutoConfigureRestDocs
@WebMvcTest(CouponSearchController.class)
class CouponSearchControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CouponSearchService couponSearchService;

    List<SelectOwnCouponResponseDto> ownCouponResponseTemps;

    @BeforeEach
    void beforeEach() {
        CouponTypeCashVo storeCouponTypeCashVo = createCouponTypeCashVo();
        CouponTypePercentVo storeCouponTypePercentVo = createCouponTypePercentVo();

        CouponTypeCashVo merchantCouponTypeCashVo = createCouponTypeCashVo();
        CouponTypePercentVo merchantCouponTypePercentVo = createCouponTypePercentVo();

        CouponTypeCashVo allCouponTypeCashVo = createCouponTypeCashVo();
        CouponTypePercentVo allCouponTypePercentVo = createCouponTypePercentVo();

        ownCouponResponseTemps = List.of(
            new SelectOwnCouponResponseDto(UUID.randomUUID(), storeCouponTypeCashVo, "매장",
                "매장 금액 쿠폰", "10000원 이상 시 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), storeCouponTypePercentVo,
                "매장", "매장 퍼센트 쿠폰", "10000원 이상 시 3%, 최대 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), merchantCouponTypeCashVo, "가맹점",
                "가맹점 금액 쿠폰", "10000원 이상 시 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), merchantCouponTypePercentVo,
                "가맹점", "가맹점 퍼센트 쿠폰", "10000원 이상 시 3%, 최대 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), allCouponTypeCashVo,
                "전체", "전체 금액 쿠폰", "10000원 이상 시 1000원 할인",
                LocalDate.now(), null),
            new SelectOwnCouponResponseDto(UUID.randomUUID(), allCouponTypePercentVo,
                "전체", "전체 금액 쿠폰", "10000원 이상 시 3%, 최대 1000원 할인",
                LocalDate.now(), null)
        );
    }

    @Test
    @DisplayName("소유한 쿠폰 확인 - 전체")
    void GetOwnAllCouponsTest() throws Exception {
        when(couponSearchService.getOwnCoupons(any(Long.class), any(Pageable.class), any(), any()))
            .thenAnswer(invocation -> new PageImpl<>(ownCouponResponseTemps, invocation.getArgument(1),
                ownCouponResponseTemps.size()));

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/coupon/search/{accountId}", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getOwnCoupons",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("getOwnCoupons.Response")),
                pathParameters(parameterWithName("accountId").description("사용자 id")),
                requestParameters(parameterWithName("usable").optional()
                        .description("사용 가능 여부 (null = 사용 가능•불가 쿠폰 모두, true = 사용 가능, false = 사용 불가)"),
                    parameterWithName("storeId").optional().description("매장 id 필터링, null = 필터링 없음"),
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("한 페이지에 몇 개의 데이터를 불러올 것인지"),
                    parameterWithName("sort").optional().description("페이징 정렬조건")
                ),
                responseFields(
                    fieldWithPath("content[].issueCouponCode").description("쿠폰 코드"),
                    fieldWithPath("content[].couponTypeResponse.type").description("쿠폰 타입 설명"),
                    fieldWithPath("content[].couponTypeResponse.discountAmount").optional().description("할인금"),
                    fieldWithPath("content[].couponTypeResponse.rate").optional().description("할인율"),
                    fieldWithPath("content[].couponTypeResponse.minimumOrderPrice").optional().description("최소주문금액"),
                    fieldWithPath("content[].couponTypeResponse.maximumDiscountAmount").optional().description("최대할인금액"),
                    fieldWithPath("content[].couponUsageName").description("쿠폰 사용처 이름"),
                    fieldWithPath("content[].name").description("쿠폰명"),
                    fieldWithPath("content[].description").description("쿠폰 설명"),
                    fieldWithPath("content[].expirationDate").description("쿠폰 만료일"),
                    fieldWithPath("content[].logTypeDescription").description("쿠폰 최근 사용 내역"),
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

    private CouponTypeCashVo createCouponTypeCashVo() {
        try {
            Constructor<CouponTypeCashVo> constructor = CouponTypeCashVo.class
                .getDeclaredConstructor(int.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(1_000, 10_000);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private CouponTypePercentVo createCouponTypePercentVo() {
        try {
            Constructor<CouponTypePercentVo> constructor = CouponTypePercentVo.class
                .getDeclaredConstructor(int.class, int.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(3, 1_000, 10_000);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
