package store.cookshoong.www.cookshoongbackend.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponExhaustionException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.CouponPolicyNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ProvideIssueCouponFailureException;
import store.cookshoong.www.cookshoongbackend.coupon.model.request.UpdateProvideCouponRequestDto;
import store.cookshoong.www.cookshoongbackend.coupon.service.ProvideCouponService;
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@AutoConfigureRestDocs
@WebMvcTest(ProvideCouponController.class)
class ProvideCouponControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProvideCouponService provideCouponService;

    @MockBean
    RabbitTemplate rabbitTemplate;

    @MockBean
    LockProcessor lockProcessor;

    TestEntity te = new TestEntity();

    UpdateProvideCouponRequestDto updateProvideCouponRequestDto;

    @BeforeEach
    void beforeEach() {
        updateProvideCouponRequestDto = te.createUsingDeclared(UpdateProvideCouponRequestDto.class);
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, "couponPolicyId", Long.MIN_VALUE);
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, "accountId", Long.MIN_VALUE);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {"쿠폰 청책 id:couponPolicyId"}, delimiter = ':')
    @DisplayName("쿠폰 발급 실패 - 필수 필드 없음")
    void postOfferCouponNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, fieldName, null);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void postOfferCouponToAccountSuccessTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        doNothing().when(provideCouponService)
            .provideCouponToAccountByApi(any(UpdateProvideCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("postOfferCouponToAccount",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("postOfferCouponToAccount.Request")),
                requestFields(
                    fieldWithPath("accountId").description("사용자 id"),
                    fieldWithPath("couponPolicyId").description("쿠폰 정책 id")
                )
            ));

        verify(provideCouponService, times(1))
            .provideCouponToAccountByApi(any(UpdateProvideCouponRequestDto.class));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 정책 미존재")
    void postOfferCouponToAccountNonPolicyFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        doThrow(CouponPolicyNotFoundException.class)
            .when(provideCouponService)
            .provideCouponToAccountByApi(any(UpdateProvideCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 없음")
    void postOfferCouponToAccountNonIssueCouponsFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        doThrow(CouponExhaustionException.class)
            .when(provideCouponService)
            .provideCouponToAccountByApi(any(UpdateProvideCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유효 쿠폰 경쟁 밀림")
    void postOfferCouponToAccountIssueFailTest() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> argument = invocation.getArgument(1);
            argument.accept(invocation.getArgument(0));
            return null;
        }).when(lockProcessor).lock(anyString(), any(Consumer.class));

        doThrow(ProvideIssueCouponFailureException.class)
            .when(provideCouponService)
            .provideCouponToAccountByApi(any(UpdateProvideCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {"쿠폰 청책 id:couponPolicyId", "사용자 id:accountId"}, delimiter = ':')
    @DisplayName("쿠폰 이벤트 발급 실패 - 필수 필드 없음")
    void postProvideCouponToAccountByEventNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(updateProvideCouponRequestDto, fieldName, null);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide/event")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 이벤트 발급 성공")
    void postProvideCouponToAccountByEventSuccessTest() throws Exception {
        doNothing().when(provideCouponService)
            .provideCouponToAccountByApi(any(UpdateProvideCouponRequestDto.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/coupon/provide/event")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateProvideCouponRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("postProvideCouponToAccountByEvent",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("postProvideCouponToAccountByEvent.Request")),
                requestFields(
                    fieldWithPath("accountId").description("사용자 id"),
                    fieldWithPath("couponPolicyId").description("쿠폰 정책 id")
                )
            ));

        verify(rabbitTemplate, times(1))
            .convertAndSend(any(UpdateProvideCouponRequestDto.class));
    }
}
