package store.cookshoong.www.cookshoongbackend.payment.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.cart.db.service.CartService;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.service.OrderService;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateRefundDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TossPaymentKeyResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.service.PaymentService;

/**
 * 결제에 대한 Controller 테스트.
 *
 * @author jeongjewan
 * @since 2023.08.03
 */
@AutoConfigureRestDocs
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private CartRedisService cartRedisService;

    @MockBean
    private CartService cartService;
    @MockBean
    private OrderService orderService;

    UUID uuidOrder = UUID.randomUUID();
    UUID uuidCharge = UUID.randomUUID();
    public static final String CART = "cartKey=";


    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장")
    void postCreatePayment() throws Exception {


        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", uuidOrder);
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");
        ReflectionTestUtils.setField(createPaymentDto, "cartKey", CART + 1);
        ReflectionTestUtils.setField(createPaymentDto, "couponCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createPaymentDto, "point", 1_000);

        mockMvc.perform(post("/api/payments/charges")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createPaymentDto)))
            .andExpect(status().isCreated())
            .andDo(document("post-create-payment",
                ResourceSnippetParameters.builder()
                    .requestSchema(schema("PostCreatePayment")),
                requestFields(
                    fieldWithPath("orderId").description("주문아이디"),
                    fieldWithPath("paymentType").description("결제타입"),
                    fieldWithPath("chargedAt").description("승인날짜"),
                    fieldWithPath("chargedAmount").description("결제금액"),
                    fieldWithPath("paymentKey").description("toss 결제 Key"),
                    fieldWithPath("cartKey").description("장바구니 key"),
                    fieldWithPath("couponCode").optional().description("사용하는 쿠폰의 코드"),
                    fieldWithPath("point").optional().description("사용하는 포인트"),
                    fieldWithPath("discountAmount").optional().description("쿠폰에 의한 할인값")
                )));

        verify(paymentService, times(1)).createPayment(any(CreatePaymentDto.class));
        verify(cartRedisService, times(1)).removeCartMenuAll(createPaymentDto.getCartKey());
        String id = createPaymentDto.getCartKey().replaceAll(CART, "");
        verify(cartService, times(1)).deleteCartDb(Long.valueOf(id));
    }

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장")
    void postCreatePayment_Validation_Exception() throws Exception {

        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", null);
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");

        mockMvc.perform(post("/api/payments/charges")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPaymentDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문에 대해서 환불되는 정보를 DB 에 저장")
    void postCreateRefund() throws Exception {


        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "orderCode", uuidOrder);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", uuidCharge);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        mockMvc.perform(post("/api/payments/refunds")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRefundDto)))
            .andExpect(status().isCreated())
            .andDo(document("post-create-refund",
                ResourceSnippetParameters.builder()
                    .requestSchema(schema("PostCreateRefund")),
                requestFields(
                    fieldWithPath("orderCode").description("주문코드"),
                    fieldWithPath("chargeCode").description("결제코드"),
                    fieldWithPath("refundAt").description("환불시간"),
                    fieldWithPath("refundAmount").description("환불금액"),
                    fieldWithPath("refundType").description("환불타입")
                )));

        verify(paymentService, times(1)).createRefund(any(CreateRefundDto.class));
        verify(orderService, times(1)).changeStatus(uuidOrder, OrderStatus.StatusCode.PARTIAL);
    }

    @Test
    @DisplayName("주문에 대해서 환불되는 정보를 DB 에 저장 실패 - 결제 코드가 null 인 경우")
    void postCreateRefund_Validation_Exception() throws Exception {

        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", null);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        mockMvc.perform(post("/api/payments/refunds")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRefundDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문에 대해서 부분 환불되는 정보를 DB 에 저장")
    void postCreateRefundPartial() throws Exception {


        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "orderCode", uuidOrder);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", uuidCharge);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        mockMvc.perform(post("/api/payments/refunds/partial")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRefundDto)))
            .andExpect(status().isCreated())
            .andDo(document("post-create-refund",
                ResourceSnippetParameters.builder()
                    .requestSchema(schema("PostCreateRefund")),
                requestFields(
                    fieldWithPath("orderCode").description("주문코드"),
                    fieldWithPath("chargeCode").description("결제코드"),
                    fieldWithPath("refundAt").description("환불시간"),
                    fieldWithPath("refundAmount").description("환불금액"),
                    fieldWithPath("refundType").description("환불타입")
                )));

        verify(paymentService, times(1)).createRefund(any(CreateRefundDto.class));
        verify(orderService, times(1)).changeStatus(uuidOrder, OrderStatus.StatusCode.PARTIAL);
    }

    @Test
    @DisplayName("주문에 대해서 부분 환불되는 정보를 DB 에 저장")
    void postCreateRefundPartial_Validation_Exception() throws Exception {


        CreateRefundDto createRefundDto = ReflectionUtils.newInstance(CreateRefundDto.class);
        ReflectionTestUtils.setField(createRefundDto, "orderCode", null);
        ReflectionTestUtils.setField(createRefundDto, "chargeCode", uuidCharge);
        ReflectionTestUtils.setField(createRefundDto, "refundAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createRefundDto, "refundAmount", 20000);
        ReflectionTestUtils.setField(createRefundDto, "refundType", "partial");

        mockMvc.perform(post("/api/payments/refunds/partial")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRefundDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("토스 API 결제 조회 및 취소에 필요한 paymentKey 를 조회")
    void getTossPaymentKey() throws Exception {
        TossPaymentKeyResponseDto tossPaymentKeyResponseDto = new TossPaymentKeyResponseDto("paymentKey");

        mockMvc.perform(get("/api/payments/{orderCode}/select-paymentKey", uuidOrder)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tossPaymentKeyResponseDto)))
            .andExpect(status().isOk())
            .andDo(document("get-toss-payment-key",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("orderCode").description("주문코드"))
                    .requestSchema(schema("GetTossPaymentKey")),
                requestFields(
                    fieldWithPath("paymentKey").description("결제키")
                )));
    }

    @Test
    @DisplayName("해당 결제에 대해 환불금액이 결제금액보다 넘어가는지 검증")
    void getIsRefundAmountExceedsChargedAmount() throws Exception {

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/payments/charges/{chargeCode}/refunds/verify", uuidCharge)
            .param("refundAmount", "20000")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andDo(document("get-is-refundAmount-exceeds-chargedAmount",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("chargeCode").description("결제코드"))
                    .pathParameters(parameterWithName("refundAmount").description("환불금액"))
                    .requestSchema(schema("GetIsRefundAmountExceedsChargedAmount"))));
    }
}
