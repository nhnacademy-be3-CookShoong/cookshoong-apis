package store.cookshoong.www.cookshoongbackend.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
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
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;
import store.cookshoong.www.cookshoongbackend.coupon.exception.AlreadyUsedCouponException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.ExpiredCouponException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.IssueCouponNotFoundException;
import store.cookshoong.www.cookshoongbackend.coupon.exception.NonIssuedCouponProperlyException;
import store.cookshoong.www.cookshoongbackend.coupon.service.ProvideCouponService;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.BelowMinimumOrderPriceException;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.service.OrderService;
import store.cookshoong.www.cookshoongbackend.point.service.PointService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@AutoConfigureRestDocs
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    OrderService orderService;
    @MockBean
    CartRedisService cartRedisService;
    @MockBean
    ProvideCouponService provideCouponService;
    @MockBean
    AddressService addressService;
    @MockBean
    StoreService storeService;
    @MockBean
    PointService pointService;

    TestEntity te = new TestEntity();

    CreateOrderRequestDto createOrderRequestDto;

    @BeforeEach
    void beforeEach() {
        createOrderRequestDto = te.createUsingDeclared(CreateOrderRequestDto.class);
        ReflectionTestUtils.setField(createOrderRequestDto, "orderCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createOrderRequestDto, "accountId", 52L);
        ReflectionTestUtils.setField(createOrderRequestDto, "storeId", 33L);
        ReflectionTestUtils.setField(createOrderRequestDto, "memo", "김치 많이 주세요");
        ReflectionTestUtils.setField(createOrderRequestDto, "issueCouponCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createOrderRequestDto, "pointAmount", 1_000);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {
        "주문번호:orderCode", "사용자 id:accountId", "매장 id:storeId"
    }, delimiter = ':')
    @DisplayName("주문 생성 실패 - 필수 필드 없음")
    void postOrderNonFieldFailTest(String displayName, String fieldName) throws Exception {
        ReflectionTestUtils.setField(createOrderRequestDto, fieldName, null);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 주문 거리 벗어남")
    void postOrderOutOfDistanceFailTest() throws Exception {
        AddressResponseDto addressResponseDto = mock(AddressResponseDto.class);
        when(addressService.selectAccountAddressRenewalAt(anyLong()))
            .thenReturn(addressResponseDto);

        when(storeService.isInStandardDistance(any(AddressResponseDto.class), anyLong()))
            .thenReturn(false);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(storeService, Mockito.times(1))
            .isInStandardDistance(any(AddressResponseDto.class), anyLong());
    }

    @ParameterizedTest
    @ValueSource(classes = {
        IssueCouponNotFoundException.class,
        AlreadyUsedCouponException.class,
        NonIssuedCouponProperlyException.class})
    @DisplayName("주문 생성 실패 - 발급 쿠폰 검증 실패")
    void postOrderValidProvideCouponFailTest(Class<? extends Throwable> clazz) throws Exception {
        when(storeService.isInStandardDistance(any(), anyLong()))
            .thenReturn(true);

        doThrow(clazz).when(provideCouponService)
            .validProvideCoupon(any(UUID.class), anyLong());

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().is4xxClientError());

        verify(provideCouponService, Mockito.times(1))
            .validProvideCoupon(any(UUID.class), anyLong());
    }

    @ParameterizedTest
    @ValueSource(classes = {
        IssueCouponNotFoundException.class,
        BelowMinimumOrderPriceException.class})
    @DisplayName("주문 생성 실패 - 최소 주문 금액 검증 실패")
    void postOrderValidMinimumOrderPriceFailTest(Class<? extends Throwable> clazz) throws Exception {
        when(storeService.isInStandardDistance(any(), anyLong()))
            .thenReturn(true);

        doThrow(clazz).when(provideCouponService)
            .validMinimumOrderPrice(any(UUID.class), anyInt());

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().is4xxClientError());

        verify(provideCouponService, Mockito.times(1))
            .validMinimumOrderPrice(any(UUID.class), anyInt());
    }

    @ParameterizedTest
    @ValueSource(classes = {
        IssueCouponNotFoundException.class,
        ExpiredCouponException.class})
    @DisplayName("주문 생성 실패 - 만료 시간 검증 실패")
    void postOrderValidExpirationDateTimeFailTest(Class<? extends Throwable> clazz) throws Exception {
        when(storeService.isInStandardDistance(any(), anyLong()))
            .thenReturn(true);

        doThrow(clazz).when(provideCouponService)
            .validExpirationDateTime(any(UUID.class));

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().is4xxClientError());

        verify(provideCouponService, Mockito.times(1))
            .validExpirationDateTime(any(UUID.class));
    }

    @Test
    @DisplayName("주문 생성 성공")
    void postOrderSuccessTest() throws Exception {
        when(storeService.isInStandardDistance(any(), anyLong()))
            .thenReturn(true);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("postOrder",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("postOrder.Request"))
                    .responseSchema(Schema.schema("postOrder.Response")),
                requestFields(
                    fieldWithPath("orderCode").description("쿠폰명"),
                    fieldWithPath("accountId").description("주문자 id"),
                    fieldWithPath("storeId").description("매장 id"),
                    fieldWithPath("memo").optional().description("주문 요청사항"),
                    fieldWithPath("issueCouponCode").optional().description("쿠폰 코드"),
                    fieldWithPath("pointAmount").optional().description("사용 포인트")),
                responseFields(
                    fieldWithPath("totalPrice").description("총합금액")
                )
            ));

        verify(orderService, Mockito.times(1))
            .createOrder(any(CreateOrderRequestDto.class), anyList());
    }
}
