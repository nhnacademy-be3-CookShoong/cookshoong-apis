package store.cookshoong.www.cookshoongbackend.cart.redis.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.CreateCartRedisValidationException;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.ModifyCartMenuValidationException;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuCountDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;

/**
 * Redis 장바구니에 대한 Controller 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.23
 */
@AutoConfigureRestDocs
@WebMvcTest(CartRedisController.class)
class CartRedisControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartRedisService cartRedisService;

    String redisKey = "ded3e890-1c32-4dbc-bf35-55152b48c11d";
    String hashKey = "1:1,2";
    Long accountId = 1L;
    Long storeId = 1L;
    String storeName = "네네치킨";
    Long menuId = 1L;
    CartMenuDto cartMenuDto;
    CartOptionDto cartOptionDto;
    CartOptionDto cartOptionDto1;
    List<CartOptionDto> cartOptionDtos;
    CartRedisDto cartRedisDto;

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
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
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
                    fieldWithPath("menu.menuId").description("메뉴아이디"),
                    fieldWithPath("menu.menuName").description("메뉴이름"),
                    fieldWithPath("menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("options[].optionId").description("옵션아이"),
                    fieldWithPath("options[].optionName").description("옵션이름"),
                    fieldWithPath("options[].optionPrice").description("옵션가격"),
                    fieldWithPath("createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("hashKey").description("메뉴 hashKey")
                )));
    }

    @Test
    @DisplayName("장바구니에 메뉴 등록 - validation 오류 accountId 가 존재하지 않을 때")
    void postCreateCart_InvalidInput_ThrowsCreateCartRedisValidationException() throws Exception {
        ReflectionTestUtils.setField(cartRedisDto, "accountId", null);

        // Act & Assert
        mockMvc.perform(post("/api/carts/{cartKey}/add-menu/{menuKey}", "testCart", "testMenu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(cartRedisDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니에 메뉴 수정")
    void putModifyCartMenu() throws Exception {

        CartOptionDto cartOptionDto2 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 3L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "사이다");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 3000);

        CartOptionDto cartOptionDto3 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 4L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "불닭소스");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 400);


        List<CartOptionDto> cartOptions = new ArrayList<>();
        cartOptions.add(cartOptionDto2);
        cartOptions.add(cartOptionDto3);

        CartRedisDto cartRedisDto2 = ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto2, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto2, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto2, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto2, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto2, "options", cartOptions);
        ReflectionTestUtils.setField(cartRedisDto2, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto2, "hashKey", "1:3,4");

        mockMvc.perform(put("/api/carts/{cartKey}/modify-menu/{menuKey}", redisKey, "1:3,4")
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
                    fieldWithPath("menu.menuId").description("메뉴아이디"),
                    fieldWithPath("menu.menuName").description("메뉴이름"),
                    fieldWithPath("menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("options[].optionId").description("옵션아이"),
                    fieldWithPath("options[].optionName").description("옵션이름"),
                    fieldWithPath("options[].optionPrice").description("옵션가격"),
                    fieldWithPath("createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("hashKey").description("메뉴 hashKey")
                )));
    }

    @Test
    void putModifyCartMenu_InvalidInput_ThrowsModifyCartMenuValidationException() throws Exception {

        ReflectionTestUtils.setField(cartRedisDto, "accountId", null);

        // Act & Assert
        mockMvc.perform(put("/api/carts/{cartKey}/modify-menu/{menuKey}", "testCart", "testMenu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(cartRedisDto)));
    }

    @Test
    void getSelectCartMenuAll() throws Exception {

        List<CartRedisDto> carts = Collections.singletonList(cartRedisDto);

        given(cartRedisService.selectCartMenuAll(anyString())).willReturn(carts);

        mockMvc.perform(get("/api/carts/{cartKey}", redisKey))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountId").value(cartRedisDto.getAccountId()))
            .andExpect(jsonPath("$[0].storeId").value(cartRedisDto.getStoreId()))
            .andExpect(jsonPath("$[0].storeName").value(cartRedisDto.getStoreName()))
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
            .andDo(document("get-select-cart-menu-all",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .requestSchema(schema("GetSelectCartMenuAll")),
            responseFields(
                fieldWithPath("[].accountId").description("회원아이디"),
                fieldWithPath("[].storeId").description("매장아이디"),
                fieldWithPath("[].storeName").description("매장이름"),
                fieldWithPath("[].menu.menuId").description("메뉴아이디"),
                fieldWithPath("[].menu.menuName").description("메뉴이름"),
                fieldWithPath("[].menu.menuImage").description("메뉴이미지"),
                fieldWithPath("[].menu.menuPrice").description("메뉴가격"),
                fieldWithPath("[].options[].optionId").description("옵션아이"),
                fieldWithPath("[].options[].optionName").description("옵션이름"),
                fieldWithPath("[].options[].optionPrice").description("옵션가격"),
                fieldWithPath("[].createTimeMillis").description("메뉴담긴날짜"),
                fieldWithPath("[].hashKey").description("메뉴 hashKey")
            )));
    }

    @Test
    void getSelectCartMenu() throws Exception {

        given(cartRedisService.selectCartMenu(anyString(), anyString())).willReturn(cartRedisDto);

        mockMvc.perform(get("/api/carts/{cartKey}/menu/{menuKey}", redisKey, hashKey))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(cartRedisDto.getAccountId()))
            .andExpect(jsonPath("$.storeId").value(cartRedisDto.getStoreId()))
            .andExpect(jsonPath("$.storeName").value(cartRedisDto.getStoreName()))
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
            .andDo(document("get-select-cart-menu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .pathParameters(parameterWithName("hashKEy").description("장바구니 메뉴 HashKey"))
                    .requestSchema(schema("GetSelectCartMenu")),
                responseFields(
                    fieldWithPath("accountId").description("회원아이디"),
                    fieldWithPath("storeId").description("매장아이디"),
                    fieldWithPath("storeName").description("매장이름"),
                    fieldWithPath("menu.menuId").description("메뉴아이디"),
                    fieldWithPath("menu.menuName").description("메뉴이름"),
                    fieldWithPath("menu.menuImage").description("메뉴이미지"),
                    fieldWithPath("menu.menuPrice").description("메뉴가격"),
                    fieldWithPath("options[].optionId").description("옵션아이"),
                    fieldWithPath("options[].optionName").description("옵션이름"),
                    fieldWithPath("options[].optionPrice").description("옵션가격"),
                    fieldWithPath("createTimeMillis").description("메뉴담긴날짜"),
                    fieldWithPath("hashKey").description("메뉴 hashKey")
                )));
    }

    @Test
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
    void deleteCartMenuAll() throws Exception {

        mockMvc.perform(delete("/api/carts/{cartKey}/delete-all", redisKey))
            .andExpect(status().isNoContent())
            .andDo(document("delete-cart-menu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("cartKey").description("장바구니 Redis Key"))
                    .requestSchema(schema("DeleteCartMenu"))));
    }
}
