package store.cookshoong.www.cookshoongbackend.menu_order.controller;

import static org.mockito.ArgumentMatchers.any;
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
import java.util.List;
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
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuGroupService;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuService;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionGroupService;

@AutoConfigureRestDocs
@WebMvcTest(MenuGroupController.class)
class MenuGroupControllerTest {

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
    @DisplayName("메뉴 그룹 등록")
    void postMenuGroup() throws Exception{
        CreateMenuGroupRequestDto createMenuGroupRequestDto
            = ReflectionUtils.newInstance(CreateMenuGroupRequestDto.class);
        ReflectionTestUtils.setField(createMenuGroupRequestDto, "name", "메뉴그룹1");
        ReflectionTestUtils.setField(createMenuGroupRequestDto, "description", "메뉴그룹1설명");
        ReflectionTestUtils.setField(createMenuGroupRequestDto, "targetMenuGroupId", null);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .post("/api/stores/{storeId}/menu-group", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createMenuGroupRequestDto));
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createMenuGroup",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createMenuGroup.Request")),
                requestFields(
                    fieldWithPath("name").description("메뉴그룹 이름"),
                    fieldWithPath("description").description("메뉴그룹 설명"),
                    fieldWithPath("targetMenuGroupId").description("지정 메뉴그룹 아이디")
                    )));
    }

    @Test
    @DisplayName("메뉴 그룹 리스트 조회")
    void getMenuGroups() throws Exception{
        SelectMenuGroupResponseDto selectMenuGroupResponseDto1
            = new SelectMenuGroupResponseDto(1L, 1L, "메뉴그룹1", "메뉴그룹1설명", 0);
        SelectMenuGroupResponseDto selectMenuGroupResponseDto2
            = new SelectMenuGroupResponseDto(2L, 1L, "메뉴그룹2", "메뉴그룹2설명", 0);
        SelectMenuGroupResponseDto selectMenuGroupResponseDto3
            = new SelectMenuGroupResponseDto(3L, 1L, "메뉴그룹3", "메뉴그룹3설명", 0);
        SelectMenuGroupResponseDto selectMenuGroupResponseDto4
            = new SelectMenuGroupResponseDto(4L, 1L, "메뉴그룹4", "메뉴그룹4설명", 0);

        List<SelectMenuGroupResponseDto> selectMenuGroupResponseDtoList
            = List.of(selectMenuGroupResponseDto1, selectMenuGroupResponseDto2, selectMenuGroupResponseDto3, selectMenuGroupResponseDto4);

        when(menuGroupService.selectMenuGroups(any())).thenReturn(selectMenuGroupResponseDtoList);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/menu-group", 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(1L))
            .andExpect(jsonPath("$.[0].storeId").value(1L))
            .andExpect(jsonPath("$.[0].name").value("메뉴그룹1"))
            .andExpect(jsonPath("$.[0].description").value("메뉴그룹1설명"))
            .andExpect(jsonPath("$.[0].menuGroupSequence").value(0))
            .andExpect(jsonPath("$.[1].id").value(2L))
            .andExpect(jsonPath("$.[1].storeId").value(1L))
            .andExpect(jsonPath("$.[1].name").value("메뉴그룹2"))
            .andExpect(jsonPath("$.[1].description").value("메뉴그룹2설명"))
            .andExpect(jsonPath("$.[1].menuGroupSequence").value(0))
            .andExpect(jsonPath("$.[2].id").value(3L))
            .andExpect(jsonPath("$.[2].storeId").value(1L))
            .andExpect(jsonPath("$.[2].name").value("메뉴그룹3"))
            .andExpect(jsonPath("$.[2].description").value("메뉴그룹3설명"))
            .andExpect(jsonPath("$.[2].menuGroupSequence").value(0))
            .andExpect(jsonPath("$.[3].id").value(4L))
            .andExpect(jsonPath("$.[3].storeId").value(1L))
            .andExpect(jsonPath("$.[3].name").value("메뉴그룹4"))
            .andExpect(jsonPath("$.[3].description").value("메뉴그룹4설명"))
            .andExpect(jsonPath("$.[3].menuGroupSequence").value(0))
            .andDo(MockMvcRestDocumentationWrapper.document("selectMenuGroups",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectMenuGroups.Response")),
                responseFields(
                    fieldWithPath("[].id").description("메뉴그룹 아이디"),
                    fieldWithPath("[].storeId").description("매장 아이디"),
                    fieldWithPath("[].name").description("메뉴그룹 이름"),
                    fieldWithPath("[].description").description("메뉴그룹 설명"),
                    fieldWithPath("[].menuGroupSequence").description("메뉴그룹 순서")
                )));
    }

    @Test
    @DisplayName("메뉴 그룹 단건 조회")
    void getMenuGroup() throws Exception{
        SelectMenuGroupResponseDto selectMenuGroupResponseDto
            = new SelectMenuGroupResponseDto(1L, 1L, "메뉴그룹1", "메뉴그룹1설명", 0);

        when(menuGroupService.selectMenuGroup(any())).thenReturn(selectMenuGroupResponseDto);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/menu-group/{menuGroupId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.storeId").value(1L))
            .andExpect(jsonPath("$.name").value("메뉴그룹1"))
            .andExpect(jsonPath("$.description").value("메뉴그룹1설명"))
            .andExpect(jsonPath("$.menuGroupSequence").value(0))
            .andDo(MockMvcRestDocumentationWrapper.document("selectMenuGroup",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectMenuGroup.Response")),
                responseFields(
                    fieldWithPath("id").description("메뉴그룹 아이디"),
                    fieldWithPath("storeId").description("매장 아이디"),
                    fieldWithPath("name").description("메뉴그룹 이름"),
                    fieldWithPath("description").description("메뉴그룹 설명"),
                    fieldWithPath("menuGroupSequence").description("메뉴그룹 순서")
                )));
    }

    @Test
    @DisplayName("메뉴 그룹 삭제")
    void deleteMenuGroup() throws Exception {
        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .delete("/api/stores/{storeId}/menu-group/{menuGroupId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("removeMenuGroup",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("menuGroupId").description("메뉴 그룹 아이디"))
                    .requestSchema(Schema.schema("deleteMenuGroup.Request"))));
    }
}
