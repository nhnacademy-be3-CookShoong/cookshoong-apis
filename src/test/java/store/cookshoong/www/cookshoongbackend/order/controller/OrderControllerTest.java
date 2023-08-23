package store.cookshoong.www.cookshoongbackend.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.model.request.PatchOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderDetailMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.LookupOrderInStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.order.model.response.SelectOrderPossibleResponseDto;
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

    List<LookupOrderInStatusResponseDto> orders;

    @BeforeEach
    void beforeEach() {
        createOrderRequestDto = te.createUsingDeclared(CreateOrderRequestDto.class);
        ReflectionTestUtils.setField(createOrderRequestDto, "orderCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createOrderRequestDto, "accountId", 52L);
        ReflectionTestUtils.setField(createOrderRequestDto, "storeId", 33L);
        ReflectionTestUtils.setField(createOrderRequestDto, "deliveryAddress", "충남 아산시");
        ReflectionTestUtils.setField(createOrderRequestDto, "memo", "김치 많이 주세요");
        ReflectionTestUtils.setField(createOrderRequestDto, "issueCouponCode", UUID.randomUUID());
        ReflectionTestUtils.setField(createOrderRequestDto, "pointAmount", 1_000);
        ReflectionTestUtils.setField(createOrderRequestDto, "deliveryCost", 4_000);

        LookupOrderDetailMenuResponseDto orderDetail = new LookupOrderDetailMenuResponseDto(1L, "타코야키", 30, 1, 6_000);
        orderDetail.updateSelectOrderDetailMenuOptions(List.of(new LookupOrderDetailMenuOptionResponseDto("소스 많이", 500)));
        LookupOrderInStatusResponseDto order = new LookupOrderInStatusResponseDto(
            UUID.randomUUID(), "결제완료",
            List.of(orderDetail),
            "맛있게 만들어주세요", UUID.randomUUID(), 6_500, "payment-key", LocalDateTime.now(), "충남 아산시"
        );

        orders = List.of(order);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @CsvSource(value = {
        "주문번호:orderCode", "사용자 id:accountId", "매장 id:storeId", "배송주소:deliveryAddress"
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

        when(cartRedisService.getTotalPrice(anyList()))
            .thenReturn(100_000);

        when(storeService.selectStoreDeliveryCost(anyLong()))
            .thenReturn(4_000);

        when(provideCouponService.getDiscountPrice(createOrderRequestDto.getIssueCouponCode(), 100_000))
            .thenReturn(90_000);

        when(pointService.getValidPoint(createOrderRequestDto.getAccountId(), createOrderRequestDto.getPointAmount(), 94_000))
            .thenReturn(1_000);

        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalPrice").value(93_000))
            .andDo(MockMvcRestDocumentationWrapper.document("postOrder",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("postOrder.Request"))
                    .responseSchema(Schema.schema("postOrder.Response")),
                requestFields(
                    fieldWithPath("orderCode").description("주문 코드"),
                    fieldWithPath("accountId").description("주문자 id"),
                    fieldWithPath("storeId").description("매장 id"),
                    fieldWithPath("memo").optional().description("주문 요청사항"),
                    fieldWithPath("issueCouponCode").optional().description("쿠폰 코드"),
                    fieldWithPath("pointAmount").optional().description("사용 포인트"),
                    fieldWithPath("deliveryAddress").optional().description("충남 아산시"),
                    fieldWithPath("deliveryCost").optional().description("배달비")
                ),
                responseFields(
                    fieldWithPath("totalPrice").description("총합금액")
                )
            ));

        verify(orderService, Mockito.times(1))
            .createOrder(any(CreateOrderRequestDto.class), anyList());
    }

    @Test
    @DisplayName("주문 생성 성공 - 쿠폰, 포인트 미사용")
    void postOrderNonCouponSuccessTest() throws Exception {
        ReflectionTestUtils.setField(createOrderRequestDto, "issueCouponCode", null);
        ReflectionTestUtils.setField(createOrderRequestDto, "pointAmount", null);

        when(storeService.isInStandardDistance(any(), anyLong()))
            .thenReturn(true);

        when(cartRedisService.getTotalPrice(anyList()))
            .thenReturn(100_000);

        when(storeService.selectStoreDeliveryCost(anyLong()))
            .thenReturn(4_000);


        RequestBuilder request = RestDocumentationRequestBuilders
            .post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalPrice").value(104_000));

        verify(orderService, Mockito.times(1))
            .createOrder(any(CreateOrderRequestDto.class), anyList());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 주문 코드 없음")
    void patchOrderStatusNonOrderCodeFailTest() throws Exception {
        PatchOrderRequestDto patchOrderRequestDto = te.createUsingDeclared(PatchOrderRequestDto.class);
        ReflectionTestUtils.setField(patchOrderRequestDto, "statusCode", OrderStatus.StatusCode.CANCEL);

        doNothing().when(orderService)
            .changeStatus(patchOrderRequestDto.getOrderCode(), patchOrderRequestDto.getStatusCode());

        RequestBuilder request = RestDocumentationRequestBuilders
            .patch("/api/orders/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patchOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 상태 코드 없음")
    void patchOrderStatusNonStatusCodeFailTest() throws Exception {
        PatchOrderRequestDto patchOrderRequestDto = te.createUsingDeclared(PatchOrderRequestDto.class);
        ReflectionTestUtils.setField(patchOrderRequestDto, "orderCode", UUID.randomUUID());

        doNothing().when(orderService)
            .changeStatus(patchOrderRequestDto.getOrderCode(), patchOrderRequestDto.getStatusCode());

        RequestBuilder request = RestDocumentationRequestBuilders
            .patch("/api/orders/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patchOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 상태 변경")
    void patchOrderStatusTest() throws Exception {
        PatchOrderRequestDto patchOrderRequestDto = te.createUsingDeclared(PatchOrderRequestDto.class);
        ReflectionTestUtils.setField(patchOrderRequestDto, "orderCode", UUID.randomUUID());
        ReflectionTestUtils.setField(patchOrderRequestDto, "statusCode", OrderStatus.StatusCode.CANCEL);

        doNothing().when(orderService)
            .changeStatus(patchOrderRequestDto.getOrderCode(), patchOrderRequestDto.getStatusCode());

        RequestBuilder request = RestDocumentationRequestBuilders
            .patch("/api/orders/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(patchOrderRequestDto));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(MockMvcRestDocumentationWrapper.document("patchOrderStatus",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("patchOrderStatus.Request")),
                requestFields(
                    fieldWithPath("orderCode").description("주문명"),
                    fieldWithPath("statusCode").description("변경하고자 하는 주문 상태")
                )
            ));

        verify(orderService, Mockito.times(1))
            .changeStatus(patchOrderRequestDto.getOrderCode(), patchOrderRequestDto.getStatusCode());
    }

    @Test
    @DisplayName("주문 확인")
    void getOrderInProgressTest() throws Exception {
        when(orderService.lookupOrderInProgress(anyLong()))
            .thenReturn(orders);

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/orders/1")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getOrderInProgress",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("storeId").description("매장 id"))
                    .responseSchema(Schema.schema("getOrderInProgress.Response")),
                responseFields(
                    fieldWithPath("[].orderCode").description("주문 코드"),
                    fieldWithPath("[].orderStatusDescription").description("주문 현재 상태"),
                    fieldWithPath("[].selectOrderDetails").description("주문 상세"),
                    fieldWithPath("[].selectOrderDetails[].orderDetailId").description("주문 상세 번호"),
                    fieldWithPath("[].selectOrderDetails[].menuName").description("메뉴명"),
                    fieldWithPath("[].selectOrderDetails[].cookingTime").description("조리 시간"),
                    fieldWithPath("[].selectOrderDetails[].count").description("메뉴 주문수"),
                    fieldWithPath("[].selectOrderDetails[].cost").description("금액"),
                    fieldWithPath("[].selectOrderDetails[].selectOrderDetailMenuOptions").description("메뉴 주문 옵션"),
                    fieldWithPath("[].selectOrderDetails[].selectOrderDetailMenuOptions[].optionName").optional().description("옵션명"),
                    fieldWithPath("[].selectOrderDetails[].selectOrderDetailMenuOptions[].price").optional().description("옵션 가격"),
                    fieldWithPath("[].selectOrderDetails[].totalCost").description("총액"),
                    fieldWithPath("[].memo").description("요구사항"),
                    fieldWithPath("[].chargeCode").description("결제 코드"),
                    fieldWithPath("[].chargedAmount").description("결제 금액"),
                    fieldWithPath("[].paymentKey").description("결제 키"),
                    fieldWithPath("[].orderedAt").description("주문 일시"),
                    fieldWithPath("[].deliveryAddress").description("배송 주소")
                )
            ));

        verify(orderService, Mockito.times(1))
            .lookupOrderInProgress(anyLong());
    }

    @Test
    @DisplayName("완료 주문 확인")
    void getOrderInCompleteTest() throws Exception {
        when(orderService.lookupOrderInComplete(anyLong(), any(Pageable.class)))
            .thenAnswer(invocation -> new PageImpl<>(orders, invocation.getArgument(1), orders.size()));

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/orders/1/complete")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getOrderInComplete",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("storeId").description("매장 id"))
                    .requestParameters(
                        parameterWithName("page").optional().description("페이지 번호"),
                        parameterWithName("size").optional().description("한 페이지에 몇 개의 데이터를 불러올 것인지"),
                        parameterWithName("sort").optional().description("페이징 정렬조건"))
                    .responseSchema(Schema.schema("getOrderInComplete.Response")),
                responseFields(
                    fieldWithPath("content[].orderCode").description("주문 코드"),
                    fieldWithPath("content[].orderStatusDescription").description("주문 현재 상태"),
                    fieldWithPath("content[].selectOrderDetails").description("주문 상세"),
                    fieldWithPath("content[].selectOrderDetails[].orderDetailId").description("주문 상세 번호"),
                    fieldWithPath("content[].selectOrderDetails[].menuName").description("메뉴명"),
                    fieldWithPath("content[].selectOrderDetails[].cookingTime").description("조리 시간"),
                    fieldWithPath("content[].selectOrderDetails[].count").description("메뉴 주문수"),
                    fieldWithPath("content[].selectOrderDetails[].cost").description("금액"),
                    fieldWithPath("content[].selectOrderDetails[].selectOrderDetailMenuOptions").description("메뉴 주문 옵션"),
                    fieldWithPath("content[].selectOrderDetails[].selectOrderDetailMenuOptions[].optionName").optional().description("옵션명"),
                    fieldWithPath("content[].selectOrderDetails[].selectOrderDetailMenuOptions[].price").optional().description("옵션 가격"),
                    fieldWithPath("content[].selectOrderDetails[].totalCost").description("총액"),
                    fieldWithPath("content[].memo").description("요구사항"),
                    fieldWithPath("content[].chargeCode").description("결제 코드"),
                    fieldWithPath("content[].chargedAmount").description("결제 금액"),
                    fieldWithPath("content[].paymentKey").description("결제 키"),
                    fieldWithPath("content[].orderedAt").description("주문 일시"),
                    fieldWithPath("content[].deliveryAddress").description("배송 주소"),
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

        verify(orderService, Mockito.times(1))
            .lookupOrderInComplete(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("주문 가능 여부 확인")
    void getOrderPossibleTest() throws Exception {
        when(orderService.selectOrderPossible(anyBoolean(), anyLong(), anyInt()))
            .thenReturn(new SelectOrderPossibleResponseDto(false, "주문할 수 없는 거리입니다.\n총 금액이 최소 주문 금액 미만입니다.\n영업중인 매장이 아닙니다."));

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/orders/1/possible/2")
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getOrderPossible",
                ResourceSnippetParameters.builder()
                    .pathParameters(
                        parameterWithName("storeId").description("매장 id"),
                        parameterWithName("accountId").description("사용자 id")
                    )
                    .responseSchema(Schema.schema("getOrderPossible.Response")),
                responseFields(
                    fieldWithPath("orderPossible").description("주문 가능 여부"),
                    fieldWithPath("explain").description("여부 설명")

                )
            ));

        verify(orderService, Mockito.times(1))
            .selectOrderPossible(anyBoolean(), anyLong(), anyInt());
    }
}
