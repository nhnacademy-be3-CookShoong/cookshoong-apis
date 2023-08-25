package store.cookshoong.www.cookshoongbackend.menu_order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuGroupService;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuService;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionGroupService;

@AutoConfigureRestDocs
@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MenuService menuService;

    @MockBean
    MenuGroupService menuGroupService;

    @MockBean
    OptionGroupService optionGroupService;


    @Test
    @DisplayName("메뉴 그룹 리스트 조회")
    void getMenuGroups() throws Exception{

        SelectMenuResponseDto selectMenuResponseDto1
            = new SelectMenuResponseDto(1L, "OPEN", 1L, "메뉴이름1", 4000, "메뉴설명1",
            UUID.randomUUID() + ".png", 40, new BigDecimal("1.0"), "objectStorage", FileDomain.MENU_IMAGE.getVariable());
        SelectMenuResponseDto selectMenuResponseDto2
            = new SelectMenuResponseDto(2L, "OPEN", 1L, "메뉴이름2", 4000, "메뉴설명2",
            UUID.randomUUID() + ".png", 40, new BigDecimal("1.0"), "objectStorage", FileDomain.MENU_IMAGE.getVariable());
        SelectMenuResponseDto selectMenuResponseDto3
            = new SelectMenuResponseDto(3L, "OPEN", 1L, "메뉴이름3", 4000, "메뉴설명3",
            UUID.randomUUID() + ".png", 40, new BigDecimal("1.0"), "objectStorage", FileDomain.MENU_IMAGE.getVariable());
        SelectMenuResponseDto selectMenuResponseDto4
            = new SelectMenuResponseDto(4L, "OPEN", 1L, "메뉴이름4", 4000, "메뉴설명4",
            UUID.randomUUID() + ".png", 40, new BigDecimal("1.0"), "objectStorage", FileDomain.MENU_IMAGE.getVariable());

        List<SelectMenuResponseDto> selectMenuResponseDtoList
            = List.of(selectMenuResponseDto1, selectMenuResponseDto2, selectMenuResponseDto3, selectMenuResponseDto4);

        when(menuService.selectMenus(any())).thenReturn(selectMenuResponseDtoList);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/menu", 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(1L))
            .andExpect(jsonPath("$.[0].menuStatus").value("OPEN"))
            .andExpect(jsonPath("$.[0].storeId").value(1L))
            .andExpect(jsonPath("$.[0].name").value("메뉴이름1"))
            .andExpect(jsonPath("$.[0].price").value(4000))
            .andExpect(jsonPath("$.[0].description").value("메뉴설명1"))
            .andExpect(jsonPath("$.[0].cookingTime").value(40))
            .andExpect(jsonPath("$.[0].earningRate").value(new BigDecimal("1.0")))
            .andExpect(jsonPath("$.[0].locationType").value("objectStorage"))
            .andExpect(jsonPath("$.[0].domainName").value(FileDomain.MENU_IMAGE.getVariable()))
            .andExpect(jsonPath("$.[1].id").value(2L))
            .andExpect(jsonPath("$.[1].menuStatus").value("OPEN"))
            .andExpect(jsonPath("$.[1].storeId").value(1L))
            .andExpect(jsonPath("$.[1].name").value("메뉴이름2"))
            .andExpect(jsonPath("$.[1].price").value(4000))
            .andExpect(jsonPath("$.[1].description").value("메뉴설명2"))
            .andExpect(jsonPath("$.[1].cookingTime").value(40))
            .andExpect(jsonPath("$.[1].earningRate").value(new BigDecimal("1.0")))
            .andExpect(jsonPath("$.[1].locationType").value("objectStorage"))
            .andExpect(jsonPath("$.[1].domainName").value(FileDomain.MENU_IMAGE.getVariable()))
            .andExpect(jsonPath("$.[2].id").value(3L))
            .andExpect(jsonPath("$.[2].menuStatus").value("OPEN"))
            .andExpect(jsonPath("$.[2].storeId").value(1L))
            .andExpect(jsonPath("$.[2].name").value("메뉴이름3"))
            .andExpect(jsonPath("$.[2].price").value(4000))
            .andExpect(jsonPath("$.[2].description").value("메뉴설명3"))
            .andExpect(jsonPath("$.[2].cookingTime").value(40))
            .andExpect(jsonPath("$.[2].earningRate").value(new BigDecimal("1.0")))
            .andExpect(jsonPath("$.[2].locationType").value("objectStorage"))
            .andExpect(jsonPath("$.[2].domainName").value(FileDomain.MENU_IMAGE.getVariable()))
            .andExpect(jsonPath("$.[3].id").value(4L))
            .andExpect(jsonPath("$.[3].menuStatus").value("OPEN"))
            .andExpect(jsonPath("$.[3].storeId").value(1L))
            .andExpect(jsonPath("$.[3].name").value("메뉴이름4"))
            .andExpect(jsonPath("$.[3].price").value(4000))
            .andExpect(jsonPath("$.[3].description").value("메뉴설명4"))
            .andExpect(jsonPath("$.[3].cookingTime").value(40))
            .andExpect(jsonPath("$.[3].earningRate").value(new BigDecimal("1.0")))
            .andExpect(jsonPath("$.[3].locationType").value("objectStorage"))
            .andExpect(jsonPath("$.[3].domainName").value(FileDomain.MENU_IMAGE.getVariable()))
            .andDo(MockMvcRestDocumentationWrapper.document("selectMenus",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectMenus.Response")),
                responseFields(
                    fieldWithPath("[].id").description("메뉴 아이디"),
                    fieldWithPath("[].menuStatus").description("메뉴 상태"),
                    fieldWithPath("[].storeId").description("매장 아이디"),
                    fieldWithPath("[].name").description("메뉴 이름"),
                    fieldWithPath("[].price").description("메뉴 가격"),
                    fieldWithPath("[].description").description("메뉴 설명"),
                    fieldWithPath("[].savedName").description("메뉴 이미지"),
                    fieldWithPath("[].cookingTime").description("메뉴 조리시간"),
                    fieldWithPath("[].earningRate").description("메뉴 적립률"),
                    fieldWithPath("[].locationType").description("메뉴 이미지 저장타입"),
                    fieldWithPath("[].domainName").description("메뉴 도메인 이름"),
                    fieldWithPath("[].menuGroups").description("메뉴 그룹"),
                    fieldWithPath("[].optionGroups").description("옵션 그룹")
                )));
    }

    @Test
    @DisplayName("메뉴 단건 조회")
    void getMenuGroup() throws Exception{
        SelectMenuResponseDto selectMenuResponseDto
            = new SelectMenuResponseDto(1L, "OPEN", 1L, "메뉴이름1", 4000, "메뉴설명1",
        UUID.randomUUID() + ".png", 40, new BigDecimal("1.0"), "objectStorage", FileDomain.MENU_IMAGE.getVariable());

        when(menuService.selectMenu(any())).thenReturn(selectMenuResponseDto);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/menu/{menuId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.menuStatus").value("OPEN"))
            .andExpect(jsonPath("$.storeId").value(1L))
            .andExpect(jsonPath("$.name").value("메뉴이름1"))
            .andExpect(jsonPath("$.price").value(4000))
            .andExpect(jsonPath("$.description").value("메뉴설명1"))
            .andExpect(jsonPath("$.cookingTime").value(40))
            .andExpect(jsonPath("$.earningRate").value(new BigDecimal("1.0")))
            .andExpect(jsonPath("$.locationType").value("objectStorage"))
            .andExpect(jsonPath("$.domainName").value(FileDomain.MENU_IMAGE.getVariable()))
            .andDo(MockMvcRestDocumentationWrapper.document("selectMenu",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectMenu.Response")),
                responseFields(
                    fieldWithPath("id").description("메뉴 아이디"),
                    fieldWithPath("menuStatus").description("메뉴 상태"),
                    fieldWithPath("storeId").description("매장 아이디"),
                    fieldWithPath("name").description("메뉴 이름"),
                    fieldWithPath("price").description("메뉴 가격"),
                    fieldWithPath("description").description("메뉴 설명"),
                    fieldWithPath("savedName").description("메뉴 이미지"),
                    fieldWithPath("cookingTime").description("메뉴 조리시간"),
                    fieldWithPath("earningRate").description("메뉴 적립률"),
                    fieldWithPath("locationType").description("메뉴 이미지 저장타입"),
                    fieldWithPath("domainName").description("메뉴 도메인 이름"),
                    fieldWithPath("menuGroups").description("메뉴 그룹"),
                    fieldWithPath("optionGroups").description("옵션 그룹")
                )));
    }

    @Test
    @DisplayName("메뉴 삭제")
    void deleteMenu() throws Exception {
        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .delete("/api/stores/{storeId}/menu/{menuId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("removeMenu",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("menuId").description("메뉴 아이디"))
                    .requestSchema(Schema.schema("deleteMenu.Request"))));
    }
}
