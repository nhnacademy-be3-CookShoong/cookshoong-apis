package store.cookshoong.www.cookshoongbackend.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalTime;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponUsageNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreateCashCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.CreatePercentCouponPolicyRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponPolicyService;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestEntityAspect;

@AutoConfigureRestDocs
@Import(TestEntity.class)
@WebMvcTest(CouponPolicyController.class)
class CouponPolicyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CouponPolicyService couponPolicyService;

    @MockBean
    TestEntityAspect testEntityAspect;

    TestEntity te = new TestEntity();

    CreateCashCouponPolicyRequestDto cashRequestDto;

    CreatePercentCouponPolicyRequestDto percentRequestDto;

    @BeforeEach
    void beforeEach() {
        cashRequestDto = te.createUsingDeclared(CreateCashCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(cashRequestDto, "name", "금액 쿠폰");
        ReflectionTestUtils.setField(cashRequestDto, "description", "설정된 금액만큼 가격을 차감합니다.");
        ReflectionTestUtils.setField(cashRequestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(cashRequestDto, "discountAmount", 5_000);
        ReflectionTestUtils.setField(cashRequestDto, "minimumPrice", 10_000);

        percentRequestDto = te.createUsingDeclared(CreatePercentCouponPolicyRequestDto.class);
        ReflectionTestUtils.setField(percentRequestDto, "name", "퍼센트 쿠폰");
        ReflectionTestUtils.setField(percentRequestDto, "description", "설정된 퍼센트만큼 가격을 차감합니다.");
        ReflectionTestUtils.setField(percentRequestDto, "expirationTime", LocalTime.of(1, 0));
        ReflectionTestUtils.setField(percentRequestDto, "rate", new BigDecimal("10.0"));
        ReflectionTestUtils.setField(percentRequestDto, "minimumPrice", 10_000);
        ReflectionTestUtils.setField(percentRequestDto, "maximumPrice", 30_000);
    }

    @Test
    @DisplayName("매장 금액 쿠폰 정책 생성 성공")
    void postStoreCashCouponPolicySuccessTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/cash", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createStoreCashCouponPolicy",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("storeId").description("매장 id"))
                    .requestSchema(Schema.schema("createStoreCashCouponPolicy.Request")),
                requestFields(
                    fieldWithPath("name").description("쿠폰명"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("expirationTime").description("만료시간"),
                    fieldWithPath("discountAmount").description("할인금액"),
                    fieldWithPath("minimumPrice").description("최소주문금액"))
            ));

        verify(couponPolicyService, Mockito.times(1))
            .createStoreCashCouponPolicy(any(Long.class), any(CreateCashCouponPolicyRequestDto.class));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {
        "쿠폰명:name", "설명:description", "만료시간:expirationTime", "할인금액:discountAmount", "최소주문금액:minimumPrice"
    }, delimiter = ':')
    @DisplayName("매장 금액 쿠폰 정책 생성 실패 - 필수 필드 없음")
    void postStoreCashCouponPolicyNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(cashRequestDto, fieldName, null);
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/cash", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStoreCashCouponPolicy(any(Long.class), any(CreateCashCouponPolicyRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1_000, 0, 100_000})
    @DisplayName("매장 금액 쿠폰 정책 생성 실패 - 할인금액 범위 초과")
    void postStoreCashCouponPolicyDiscountAmountOutOfRangeFailTest(Integer discountAmount) throws Exception {
        ReflectionTestUtils.setField(cashRequestDto, "discountAmount", discountAmount);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/cash", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStoreCashCouponPolicy(any(Long.class), any(CreateCashCouponPolicyRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1_000, 100_000})
    @DisplayName("매장 금액 쿠폰 정책 생성 실패 - 최소주문금액 범위 초과")
    void postStoreCashCouponPolicyMinimumPriceOutOfRangeFailTest(Integer minimumPrice) throws Exception {
        ReflectionTestUtils.setField(cashRequestDto, "minimumPrice", minimumPrice);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/cash", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStoreCashCouponPolicy(any(Long.class), any(CreateCashCouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("매장 퍼센트 쿠폰 정책 생성 성공")
    void postStorePercentCouponPolicySuccessTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createStorePercentCouponPolicy",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("storeId").description("매장 id"))
                    .requestSchema(Schema.schema("createStorePercentCouponPolicy.Request")),
                requestFields(
                    fieldWithPath("name").description("쿠폰명"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("expirationTime").description("만료시간"),
                    fieldWithPath("rate").description("할인율"),
                    fieldWithPath("minimumPrice").description("최소주문금액"),
                    fieldWithPath("maximumPrice").description("최대할인금액"))
            ));

        verify(couponPolicyService, Mockito.times(1))
            .createStorePercentCouponPolicy(any(Long.class), any(CreatePercentCouponPolicyRequestDto.class));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {
        "쿠폰명:name", "설명:description", "만료시간:expirationTime", "할인율:rate", "최소주문금액:minimumPrice",
        "최대할인금액:maximumPrice"}, delimiter = ':')
    @DisplayName("매장 퍼센트 쿠폰 정책 생성 실패 - 필수 필드 없음")
    void postStorePercentCouponPolicyNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(percentRequestDto, fieldName, null);
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStorePercentCouponPolicy(any(Long.class), any(CreatePercentCouponPolicyRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "100.0", "11.11111"})
    @DisplayName("매장 퍼센트 쿠폰 정책 생성 실패 - 할인율 범위 초과")
    void postStorePercentCouponPolicyRateOutOfRangeFailTest(String rate) throws Exception {
        ReflectionTestUtils.setField(percentRequestDto, "rate", new BigDecimal(rate));
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStorePercentCouponPolicy(any(Long.class), any(CreatePercentCouponPolicyRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {-10_000, 30_000})
    @DisplayName("매장 퍼센트 쿠폰 정책 생성 실패 - 최소주문금액 범위 초과")
    void postStorePercentCouponPolicyMinimumPriceOutOfRangeFailTest(int minimumPrice) throws Exception {
        ReflectionTestUtils.setField(percentRequestDto, "minimumPrice", minimumPrice);
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStorePercentCouponPolicy(any(Long.class), any(CreatePercentCouponPolicyRequestDto.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 60_000})
    @DisplayName("매장 퍼센트 쿠폰 정책 생성 실패 - 최대할인금액 범위 초과")
    void postStorePercentCouponPolicyMaximumPriceOutOfRangeFailTest(int maximumPrice) throws Exception {
        ReflectionTestUtils.setField(percentRequestDto, "maximumPrice", maximumPrice);
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/stores/{storeId}/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(couponPolicyService, Mockito.never())
            .createStorePercentCouponPolicy(any(Long.class), any(CreatePercentCouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("가맹점 금액 쿠폰 정책 생성 성공")
    void postMerchantCashCouponPolicySuccessTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/merchants/{merchantId}/cash", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createMerchantCashCouponPolicy",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("merchantId").description("가맹점 id"))
                    .requestSchema(Schema.schema("createMerchantCashCouponPolicy.Request")),
                requestFields(
                    fieldWithPath("name").description("쿠폰명"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("expirationTime").description("만료시간"),
                    fieldWithPath("discountAmount").description("할인금액"),
                    fieldWithPath("minimumPrice").description("최소주문금액"))
            ));

        verify(couponPolicyService, Mockito.times(1))
            .createMerchantCashCouponPolicy(any(Long.class), any(CreateCashCouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("가맹점 퍼센트 쿠폰 정책 생성 성공")
    void postMerchantPercentCouponPolicySuccessTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/merchants/{merchantId}/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createMerchantPercentCouponPolicy",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("merchantId").description("가맹점 id"))
                    .requestSchema(Schema.schema("createMerchantPercentCouponPolicy.Request")),
                requestFields(
                    fieldWithPath("name").description("쿠폰명"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("expirationTime").description("만료시간"),
                    fieldWithPath("rate").description("할인율"),
                    fieldWithPath("minimumPrice").description("최소주문금액"),
                    fieldWithPath("maximumPrice").description("최대할인금액"))
            ));

        verify(couponPolicyService, Mockito.times(1))
            .createMerchantPercentCouponPolicy(any(Long.class), any(CreatePercentCouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("전체 금액 쿠폰 정책 생성 성공")
    void postAllCashCouponPolicySuccessTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/all/cash")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createAllCashCouponPolicy",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createAllCashCouponPolicy.Request")),
                requestFields(
                    fieldWithPath("name").description("쿠폰명"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("expirationTime").description("만료시간"),
                    fieldWithPath("discountAmount").description("할인금액"),
                    fieldWithPath("minimumPrice").description("최소주문금액"))
            ));

        verify(couponPolicyService, Mockito.times(1))
            .createAllCashCouponPolicy(any(CreateCashCouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("전체 금액 쿠폰 정책 생성 실패 - 전체 사용 범위 엔티티 부재")
    void postAllCashCouponPolicyNonUsageAllFailTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/all/cash")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cashRequestDto));

        doThrow(CouponUsageNotFoundException.class)
            .when(couponPolicyService).createAllCashCouponPolicy(any(CreateCashCouponPolicyRequestDto.class));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(couponPolicyService, Mockito.times(1))
            .createAllCashCouponPolicy(any(CreateCashCouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("전 퍼센트 쿠폰 정책 생성 성공")
    void postAllPercentCouponPolicySuccessTest() throws Exception {
        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/policies/all/percent", Long.MIN_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(percentRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createAllPercentCouponPolicy",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createAllPercentCouponPolicy.Request")),
                requestFields(
                    fieldWithPath("name").description("쿠폰명"),
                    fieldWithPath("description").description("쿠폰 설명"),
                    fieldWithPath("expirationTime").description("만료시간"),
                    fieldWithPath("rate").description("할인율"),
                    fieldWithPath("minimumPrice").description("최소주문금액"),
                    fieldWithPath("maximumPrice").description("최대할인금액"))
            ));

        verify(couponPolicyService, Mockito.times(1))
            .createAllPercentCouponPolicy(any(CreatePercentCouponPolicyRequestDto.class));
    }
}
