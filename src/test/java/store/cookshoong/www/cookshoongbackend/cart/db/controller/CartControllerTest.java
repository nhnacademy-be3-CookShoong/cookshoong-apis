package store.cookshoong.www.cookshoongbackend.cart.db.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.cart.db.service.CartService;

/**
 * DB 장바구니에 대한 Controller 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.30
 */
@Slf4j
@AutoConfigureRestDocs
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    Long accountId = 1L;

    @Test
    @DisplayName("DB 장바구니 존재 여부를 확인")
    void getHasDbCart() throws Exception {

        mockMvc.perform(get("/api/carts/db/{accountId}/has", accountId))
            .andExpect(status().isOk())
            .andDo(document("get-has-db-cart",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("accountId").description("DB 접근할 회원 아아디"))
                    .requestSchema(schema("GetHasDbCart"))));
    }
}
