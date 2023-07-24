package store.cookshoong.www.cookshoongbackend.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponOverCountException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateIssueCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequest;
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

    UpdateProvideCouponRequest updateProvideCouponRequest;

    @BeforeEach
    void beforeEach() {
        createIssueCouponRequestDto = te.createUsingDeclared(CreateIssueCouponRequestDto.class);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "couponPolicyId", Long.MIN_VALUE);
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", 1L);

        updateProvideCouponRequest = te.createUsingDeclared(UpdateProvideCouponRequest.class);
        ReflectionTestUtils.setField(updateProvideCouponRequest, "couponPolicyId", Long.MIN_VALUE);
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

        verify(issueCouponService, times(1))
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

        verify(issueCouponService, never())
            .createIssueCoupon(any(CreateIssueCouponRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {-100, -1, 0})
    @DisplayName("쿠폰 발행 실패 - 발행량 범위 미만")
    void postIssueCouponIssueQuantityLessFailTest(Long issueQuantity) throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", issueQuantity);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createIssueCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(issueCouponService, never())
            .createIssueCoupon(any(CreateIssueCouponRequestDto.class));
    }

    @Test
    @DisplayName("쿠폰 발행 실패 - 발행량 범위 초과")
    void postIssueCouponIssueQuantityOverFailTest() throws Exception {
        ReflectionTestUtils.setField(createIssueCouponRequestDto, "issueQuantity", Long.MAX_VALUE);

        doThrow(IssueCouponOverCountException.class)
            .when(issueCouponService).createIssueCoupon(any(CreateIssueCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createIssueCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void postOfferCouponToAccountSuccessTest() throws Exception {
        doNothing().when(issueCouponService)
            .provideCouponToAccount(any(Long.class), any(UpdateProvideCouponRequest.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue/account/{accountId}", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequest));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("postOfferCouponToAccount",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("사용자 id"))
                    .requestSchema(Schema.schema("postOfferCouponToAccount.Request")),
                requestFields(
                    fieldWithPath("couponPolicyId").description("쿠폰 정책 id"))
            ));

        verify(issueCouponService, times(1))
            .provideCouponToAccount(any(Long.class), any(UpdateProvideCouponRequest.class));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 정책 미존재")
    void postOfferCouponToAccountNonPolicyFailTest() throws Exception {
        doThrow(CouponPolicyNotFoundException.class)
            .when(issueCouponService)
            .provideCouponToAccount(any(Long.class), any(UpdateProvideCouponRequest.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue/account/{accountId}", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequest));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 없음")
    void postOfferCouponToAccountNonIssueCouponsFailTest() throws Exception {
        doThrow(IssueCouponNotFoundException.class)
            .when(issueCouponService)
            .provideCouponToAccount(any(Long.class), any(UpdateProvideCouponRequest.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue/account/{accountId}", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequest));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 경쟁 밀림")
    void postOfferCouponToAccountIssueFailTest() throws Exception {
        doThrow(ProvideIssueCouponFailureException.class)
            .when(issueCouponService)
            .provideCouponToAccount(any(Long.class), any(UpdateProvideCouponRequest.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue/account/{accountId}", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequest));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {"쿠폰 청책 id:couponPolicyId"}, delimiter = ':')
    @DisplayName("쿠폰 발행 실패 - 필수 필드 없음")
    void postOfferCouponNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(updateProvideCouponRequest, fieldName, null);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/issue/account/{accountId}", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequest));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}
