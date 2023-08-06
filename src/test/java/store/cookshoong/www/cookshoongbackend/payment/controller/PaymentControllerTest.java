package store.cookshoong.www.cookshoongbackend.payment.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreatePaymentDto;
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

    UUID uuid = UUID.randomUUID();

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장")
    void postCreatePayment() throws Exception {


        CreatePaymentDto createPaymentDto = ReflectionUtils.newInstance(CreatePaymentDto.class);
        ReflectionTestUtils.setField(createPaymentDto, "orderId", uuid);
        ReflectionTestUtils.setField(createPaymentDto, "paymentType", "toss");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAt", "2023-08-03T14:30:00+00:00");
        ReflectionTestUtils.setField(createPaymentDto, "chargedAmount", 50000);
        ReflectionTestUtils.setField(createPaymentDto, "paymentKey", "paymentKey");

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
                    fieldWithPath("paymentKey").description("toss 결제 Key")
                )));

        verify(paymentService, times(1)).createPayment(any(CreatePaymentDto.class));
    }

    @Test
    @DisplayName("결제 승인 후 결제가 완료되고나서 결제 정보를 DB 에 저장")
    void postCreatePayment_validation_check() throws Exception {


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
}
