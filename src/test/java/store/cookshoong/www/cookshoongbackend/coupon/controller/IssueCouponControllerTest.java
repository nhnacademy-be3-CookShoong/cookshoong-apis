package store.cookshoong.www.cookshoongbackend.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.IssueCouponService;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@AutoConfigureRestDocs
@WebMvcTest(IssueCouponController.class)
class IssueCouponControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    IssueCouponService issueCouponService;

    TestEntity te = new TestEntity();

    CreateIssueCouponRequestDto createIssueCouponRequestDto;

    @BeforeEach
    void beforeEach() {
        createIssueCouponRequestDto = te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "couponPolicyId", Long.MIN_VALUE);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", 1L);
    }

    @Test
    @DisplayName("쿠폰 발행 성공")
    void postIssueCouponSuccessTest() throws Exception {
        doNothing().when(issueCouponService)
            .createIssueCoupon(any(CreateIssueCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createIssueCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("postIssueCoupon",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("postIssueCoupon.Request")),
                requestFields(
                    fieldWithPath("couponPolicyId").description("쿠폰 정책 id"),
                    fieldWithPath("issueQuantity").description("발행 요청 개수"))
            ));

        verify(issueCouponService, Mockito.times(1))
            .createIssueCoupon(any(CreateIssueCouponRequestDto.class));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {
        "발행량:issueQuantity", "쿠폰 청책 id:couponPolicyId"}, delimiter = ':')
    @DisplayName("쿠폰 발행 실패 - 필수 필드 없음")
    void postIssueCouponNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, fieldName, null);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createIssueCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(issueCouponService, Mockito.never())
            .createIssueCoupon(any(CreateIssueCouponRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {-100, -1, 0})
    @DisplayName("쿠폰 발행 실패 - 발행량 범위 초과")
    void postIssueCouponIssueQuantityOutOfRangeFailTest(Long issueQuantity) throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", issueQuantity);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createIssueCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(issueCouponService, Mockito.never())
            .createIssueCoupon(any(CreateIssueCouponRequestDto.class));
    }
}
