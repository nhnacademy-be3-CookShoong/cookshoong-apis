package store.cookshoong.www.cookshoongbackend.cart.redis.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundCartRedisKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuCountDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;

/**
 * Redis 장바구니에 대한 Controller 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.23
 */
@Slf4j
@AutoConfigureRestDocs
@WebMvcTest(CartRedisController.class)
class CartRedisControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartRedisService cartRedisService;

    String redisKey = "cart_account:1";
    String hashKey = "112";
    Long accountId = 1L;
    Long storeId = 1L;
    String storeName = "네네치킨";
    Integer deliveryCost = 3000;
    Integer minimumOrderPrice = 10000;
    Long menuId = 1L;
    int count = 1;
    CartMenuDto cartMenuDto;
    CartOptionDto cartOptionDto;
    CartOptionDto cartOptionDto1;
    List<CartOptionDto> cartOptionDtos;
    CartRedisDto cartRedisDto;
    private static final String NO_MENU = "NO_KEY";

    @BeforeEach
    void setUp() {
        cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", menuId);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "양념치킨");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", "menuImage");
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 19000);

        cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 1L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "콜라");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 3000);

        cartOptionDto1 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 2L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "머스타드소스");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 400);

        cartOptionDtos = new ArrayList<>();
        cartOptionDtos.add(cartOptionDto);
        cartOptionDtos.add(cartOptionDto1);

        cartRedisDto = ReflectionUtils.newInstance(CartRedisDto.class);


        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "deliveryCost", deliveryCost);
        ReflectionTestUtils.setField(cartRedisDto, "minimumOrderPrice", minimumOrderPrice);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());
    }

    @Test
    @DisplayName("장바구니에 메뉴 등록")
    void postCreateCart() throws Exception {

        mockMvc.perform(post("/api/carts/{cartKey}/add-menu/{menuKey}", redisKey, hashKey)
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(cartRedisDto)))
            .andExpect(status().isCreated())
            .andDo(document("post-create-cart",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("PostCreateCart")),
                requestFields(
                    fieldWithPath("accountId").description("회원아이디"),
                    fieldWithPath("storeId").description("매장아이디"),
                    fieldWithPath("storeName").description("매장이름"),
                    fieldWithPath("deliveryCost").description("배달비"),
                    fieldWithPath("minimumOrderPrice").description("배달최소비용"),
                    fieldWithPath("menu.menuId").description("메뉴아이디"),
                    fieldWithPath("menu.menuName").description("메뉴이름"),
                    fieldWithPath("menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("options[].optionId").description("옵션아이"),
                    fieldWithPath("options[].optionName").description("옵션이름"),
                    fieldWithPath("options[].optionPrice").description("옵션가격"),
                    fieldWithPath("createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("hashKey").description("메뉴 hashKey"),
                    fieldWithPath("count").description("메뉴 수량"),
                    fieldWithPath("menuOptName").description("메뉴+옵션명"),
                    fieldWithPath("totalMenuPrice").description("메뉴 총 가")
                )));
    }

    @Test
    @DisplayName("빈 장바구니 등록")
    void postCreateEmptyCart() throws Exception {

        mockMvc.perform(post("/api/carts/{cartKey}/add-menu/{noKey}/empty", redisKey, NO_MENU)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("post-create-empty-cart",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("noKey").description("빈 장바구니 메뉴 HashKey"))
                    .requestSchema(schema("PostCreateEmptyCart"))));
    }

    @Test
    @DisplayName("빈 장바구니 등록")
    void postDbUploadRedis() throws Exception {

        mockMvc.perform(post("/api/carts/{cartKey}/db-upload-redis/{accountId}", redisKey, accountId)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("post-db-upload-redis",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("accountId").description("회운 아이디"))
                    .requestSchema(schema("PostDbUploadRedis"))));
    }

    @Test
    @DisplayName("장바구니에 메뉴 등록 - validation 오류 accountId 가 존재하지 않을 때")
    void postCreateCart_InvalidInput_ThrowsCreateCartRedisValidationException() throws Exception {
        ReflectionTestUtils.setField(cartRedisDto, "accountId", null);

        // Act & Assert
        mockMvc.perform(post("/api/carts/{cartKey}/add-menu/{menuKey}", redisKey, hashKey)
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(cartRedisDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니에 메뉴 수정")
    void putModifyCartMenu() throws Exception {

        CartOptionDto cartOptionDto2 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto2, "optionId", 3L);
        ReflectionTestUtils.setField(cartOptionDto2, "optionName", "사이다");
        ReflectionTestUtils.setField(cartOptionDto2, "optionPrice", 3000);

        CartOptionDto cartOptionDto3 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto3, "optionId", 4L);
        ReflectionTestUtils.setField(cartOptionDto3, "optionName", "불닭소스");
        ReflectionTestUtils.setField(cartOptionDto3, "optionPrice", 400);


        List<CartOptionDto> cartOptions = new ArrayList<>();
        cartOptions.add(cartOptionDto2);
        cartOptions.add(cartOptionDto3);

        CartRedisDto cartRedisDto2 = ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto2, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto2, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto2, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto2, "deliveryCost", deliveryCost);
        ReflectionTestUtils.setField(cartRedisDto2, "minimumOrderPrice", minimumOrderPrice);
        ReflectionTestUtils.setField(cartRedisDto2, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto2, "options", cartOptions);
        ReflectionTestUtils.setField(cartRedisDto2, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto2, "hashKey", "134");
        ReflectionTestUtils.setField(cartRedisDto2, "count", count);
        ReflectionTestUtils.setField(cartRedisDto2, "menuOptName", cartRedisDto2.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto2, "totalMenuPrice", cartRedisDto2.generateTotalMenuPrice());

        mockMvc.perform(put("/api/carts/{cartKey}/modify-menu/{menuKey}", redisKey, "134")
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(cartRedisDto2)))
            .andExpect(status().isOk())
            .andDo(document("put-modify-cart-menu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("PutModifyCartMenu")),
                requestFields(
                    fieldWithPath("accountId").description("회원아이디"),
                    fieldWithPath("storeId").description("매장아이디"),
                    fieldWithPath("storeName").description("매장이름"),
                    fieldWithPath("deliveryCost").description("배달비"),
                    fieldWithPath("minimumOrderPrice").description("배달최소비용"),
                    fieldWithPath("menu.menuId").description("메뉴아이디"),
                    fieldWithPath("menu.menuName").description("메뉴이름"),
                    fieldWithPath("menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("options[].optionId").description("옵션아이"),
                    fieldWithPath("options[].optionName").description("옵션이름"),
                    fieldWithPath("options[].optionPrice").description("옵션가격"),
                    fieldWithPath("createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("hashKey").description("메뉴 hashKey"),
                    fieldWithPath("count").description("메뉴 수량"),
                    fieldWithPath("menuOptName").description("메뉴+옵션명"),
                    fieldWithPath("totalMenuPrice").description("메뉴 총 가격")
                )));
    }

    @Test
    @DisplayName("장바구니에 메뉴 수정 실패 - validation 오류 accountId 가 존재하지 않을 때")
    void putModifyCartMenu_InvalidInput_ThrowsModifyCartMenuValidationException() throws Exception {

        ReflectionTestUtils.setField(cartRedisDto, "accountId", null);

        // Act & Assert
        mockMvc.perform(put("/api/carts/{cartKey}/modify-menu/{menuKey}", redisKey, hashKey)
            .contentType(APPLICATION_JSON)
            .content(om.writeValueAsString(cartRedisDto)));
    }

    @Test
    @DisplayName("Redis 장바구니에서 해당 메뉴 수량 증가")
    void putModifyCartMenuIncrement() throws Exception {

        mockMvc.perform(put("/api/carts/{cartKey}/menu-count-up/{menuKey}", redisKey, hashKey)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("put-modify-cart-menu-increment",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("PutModifyCartMenuIncrement"))));

    }

    @Test
    @DisplayName("Redis 장바구니에서 해당 메뉴 수량 감소")
    void putModifyCartMenuDecrement() throws Exception {

        mockMvc.perform(put("/api/carts/{cartKey}/menu-count-down/{menuKey}", redisKey, hashKey)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("put-modify-cart-menu-decrement",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("PutModifyCartMenuDecrement"))));

    }

    @Test
    @DisplayName("Redis 장바구니에 들어 있는 모든 메뉴를 조회")
    void getSelectCartMenuAll() throws Exception {

        List<CartRedisDto> carts = Collections.singletonList(cartRedisDto);

        given(cartRedisService.selectCartMenuAll(anyString())).willReturn(carts);

        mockMvc.perform(get("/api/carts/{cartKey}", redisKey))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountId").value(cartRedisDto.getAccountId()))
            .andExpect(jsonPath("$[0].storeId").value(cartRedisDto.getStoreId()))
            .andExpect(jsonPath("$[0].storeName").value(cartRedisDto.getStoreName()))
            .andExpect(jsonPath("$[0].deliveryCost").value(cartRedisDto.getDeliveryCost()))
            .andExpect(jsonPath("$[0].minimumOrderPrice").value(cartRedisDto.getMinimumOrderPrice()))
            .andExpect(jsonPath("$[0].menu.menuId").value(cartRedisDto.getMenu().getMenuId()))
            .andExpect(jsonPath("$[0].menu.menuName").value(cartRedisDto.getMenu().getMenuName()))
            .andExpect(jsonPath("$[0].menu.menuImage").value(cartRedisDto.getMenu().getMenuImage()))
            .andExpect(jsonPath("$[0].menu.menuPrice").value(cartRedisDto.getMenu().getMenuPrice()))
            .andExpect(jsonPath("$[0].options[0].optionId").value(cartRedisDto.getOptions().get(0).getOptionId()))
            .andExpect(jsonPath("$[0].options[0].optionName").value(cartRedisDto.getOptions().get(0).getOptionName()))
            .andExpect(jsonPath("$[0].options[0].optionPrice").value(cartRedisDto.getOptions().get(0).getOptionPrice()))
            .andExpect(jsonPath("$[0].storeName").value(cartRedisDto.getStoreName()))
            .andExpect(jsonPath("$[0].createTimeMillis").value(cartRedisDto.getCreateTimeMillis()))
            .andExpect(jsonPath("$[0].hashKey").value(cartRedisDto.getHashKey()))
            .andExpect(jsonPath("$[0].menuOptName").value(cartRedisDto.getMenuOptName()))
            .andExpect(jsonPath("$[0].totalMenuPrice").value(cartRedisDto.getTotalMenuPrice()))
            .andDo(document("get-select-cart-menu-all",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .requestSchema(schema("GetSelectCartMenuAll")),
                responseFields(
                    fieldWithPath("[].accountId").description("회원아이디"),
                    fieldWithPath("[].storeId").description("매장아이디"),
                    fieldWithPath("[].storeName").description("매장이름"),
                    fieldWithPath("[].deliveryCost").description("배달비"),
                    fieldWithPath("[].minimumOrderPrice").description("배달최소비용"),
                    fieldWithPath("[].menu.menuId").description("메뉴아이디"),
                    fieldWithPath("[].menu.menuName").description("메뉴이름"),
                    fieldWithPath("[].menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("[].menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("[].options[].optionId").description("옵션아이"),
                    fieldWithPath("[].options[].optionName").description("옵션이름"),
                    fieldWithPath("[].options[].optionPrice").description("옵션가격"),
                    fieldWithPath("[].createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("[].hashKey").description("메뉴 hashKey"),
                    fieldWithPath("[].count").description("메뉴 수량"),
                    fieldWithPath("[].menuOptName").description("메뉴+옵션명"),
                    fieldWithPath("[].totalMenuPrice").description("메뉴 총 가격")
                )));
    }

    @Test
    @DisplayName("Redis 장바구니에 들어 있는 해당 메뉴를 조회")
    void getSelectCartMenu() throws Exception {

        given(cartRedisService.selectCartMenu(anyString(), anyString())).willReturn(cartRedisDto);

        mockMvc.perform(get("/api/carts/{cartKey}/menu/{menuKey}", redisKey, hashKey))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(cartRedisDto.getAccountId()))
            .andExpect(jsonPath("$.storeId").value(cartRedisDto.getStoreId()))
            .andExpect(jsonPath("$.storeName").value(cartRedisDto.getStoreName()))
            .andExpect(jsonPath("$.deliveryCost").value(cartRedisDto.getDeliveryCost()))
            .andExpect(jsonPath("$.minimumOrderPrice").value(cartRedisDto.getMinimumOrderPrice()))
            .andExpect(jsonPath("$.menu.menuId").value(cartRedisDto.getMenu().getMenuId()))
            .andExpect(jsonPath("$.menu.menuName").value(cartRedisDto.getMenu().getMenuName()))
            .andExpect(jsonPath("$.menu.menuImage").value(cartRedisDto.getMenu().getMenuImage()))
            .andExpect(jsonPath("$.menu.menuPrice").value(cartRedisDto.getMenu().getMenuPrice()))
            .andExpect(jsonPath("$.options[0].optionId").value(cartRedisDto.getOptions().get(0).getOptionId()))
            .andExpect(jsonPath("$.options[0].optionName").value(cartRedisDto.getOptions().get(0).getOptionName()))
            .andExpect(jsonPath("$.options[0].optionPrice").value(cartRedisDto.getOptions().get(0).getOptionPrice()))
            .andExpect(jsonPath("$.storeName").value(cartRedisDto.getStoreName()))
            .andExpect(jsonPath("$.createTimeMillis").value(cartRedisDto.getCreateTimeMillis()))
            .andExpect(jsonPath("$.hashKey").value(cartRedisDto.getHashKey()))
            .andExpect(jsonPath("$.menuOptName").value(cartRedisDto.getMenuOptName()))
            .andExpect(jsonPath("$.totalMenuPrice").value(cartRedisDto.getTotalMenuPrice()))
            .andDo(document("get-select-cart-menu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("GetSelectCartMenu")),
                responseFields(
                    fieldWithPath("accountId").description("회원아이디"),
                    fieldWithPath("storeId").description("매장아이디"),
                    fieldWithPath("storeName").description("매장이름"),
                    fieldWithPath("deliveryCost").description("배달비"),
                    fieldWithPath("minimumOrderPrice").description("배달최소비용"),
                    fieldWithPath("menu.menuId").description("메뉴아이디"),
                    fieldWithPath("menu.menuName").description("메뉴이름"),
                    fieldWithPath("menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("options[].optionId").description("옵션아이"),
                    fieldWithPath("options[].optionName").description("옵션이름"),
                    fieldWithPath("options[].optionPrice").description("옵션가격"),
                    fieldWithPath("createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("hashKey").description("메뉴 hashKey"),
                    fieldWithPath("count").description("메뉴 수량"),
                    fieldWithPath("menuOptName").description("메뉴+옵션명"),
                    fieldWithPath("totalMenuPrice").description("메뉴 총 가격")
                )));
    }


    @Test
    @DisplayName("장바구니에 메뉴 조회 실패 - 장바구니 키가 존재하지 않을 ")
    void getSelectCartMenu_NotFoundRedisKey() throws Exception {

        NotFoundCartRedisKey exception = new NotFoundCartRedisKey();

        when(cartRedisService.selectCartMenu(redisKey, hashKey)).thenThrow(exception);

        mockMvc.perform(get("/api/carts/{cartKey}/menu/{menuKey}", redisKey, hashKey)
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(cartRedisDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Redis 장바구니에 들어 있는 메뉴에 수")
    void getSelectCartCount() throws Exception {

        Long cartCount = 5L;
        CartMenuCountDto cartMenuCount = new CartMenuCountDto(cartCount);

        given(cartRedisService.selectCartCount(redisKey)).willReturn(cartMenuCount.getCount());

        mockMvc.perform(get("/api/carts/{cartKey}/counts", redisKey))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count").value(cartMenuCount.getCount()))
            .andDo(document("get-select-cart-count",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .requestSchema(schema("GetSelectCartCount")),
                responseFields(
                    fieldWithPath("count").description("장바구니에 담긴 메뉴 수")
                )));
    }

    @Test
    @DisplayName("Redis 장바구니 redis Key 존재여부 확인")
    void getExistKeyInCartRedis() throws Exception {

        mockMvc.perform(get("/api/carts/redis/{cartKey}/exist", redisKey))
            .andExpect(status().isOk())
            .andDo(document("get-exist-key-in-cart-redis",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .requestSchema(schema("GetExistKeyInCartRedis"))));
    }

    @Test
    @DisplayName("Redis 장바구니 redis Key 존재여부 확인")
    void getExistMenuInCartRedis() throws Exception {

        mockMvc.perform(get("/api/carts/redis/{cartKey}/exist/{menuKey}/menu", redisKey, hashKey))
            .andExpect(status().isOk())
            .andDo(document("get-exist-menu-in-cart-redis",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKey").description("장바구니 Redis HashKey"))
                    .requestSchema(schema("GetExistMenuInCartRedis"))));
    }

    @Test
    @DisplayName("Redis 장바구니에 key 에 해당 메뉴를 삭제")
    void deleteCartMenu() throws Exception {

        mockMvc.perform(delete("/api/carts/{cartKey}/menu-delete/{menuKey}", redisKey, hashKey))
            .andExpect(status().isNoContent())
            .andDo(document("delete-cart-menu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("DeleteCartMenu"))));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    @DisplayName("Redis 장바구니에 key 에 모든 메뉴를 삭제하는 메서드")
    void deleteCartMenuAll() throws Exception {

        mockMvc.perform(delete("/api/carts/{cartKey}/delete-all", redisKey))
            .andExpect(status().isNoContent())
            .andDo(document("delete-cart-menu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .requestSchema(schema("DeleteCartMenu"))));
    }
}
