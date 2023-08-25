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
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionGroupService;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionService;

@AutoConfigureRestDocs
@WebMvcTest(OptionGroupController.class)
class OptionGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    OptionService optionService;

    @MockBean
    OptionGroupService optionGroupService;


    @Test
    @DisplayName("옵션 그룹 등록")
    void postOptionGroup() throws Exception{
        CreateOptionGroupRequestDto createOptionGroupRequestDto
            = ReflectionUtils.newInstance(CreateOptionGroupRequestDto.class);
        ReflectionTestUtils.setField(createOptionGroupRequestDto, "name", "옵션그룹1");
        ReflectionTestUtils.setField(createOptionGroupRequestDto, "minSelectCount", 0);
        ReflectionTestUtils.setField(createOptionGroupRequestDto, "maxSelectCount", 0);
        ReflectionTestUtils.setField(createOptionGroupRequestDto, "targetOptionGroupId", null);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .post("/api/stores/{storeId}/option-group", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOptionGroupRequestDto));
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createOptionGroup",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createOptionGroup.Request")),
                requestFields(
                    fieldWithPath("name").description("옵션 그룹 이름"),
                    fieldWithPath("minSelectCount").description("옵션 최소 선택 수"),
                    fieldWithPath("maxSelectCount").description("옵션 최대 선택 수"),
                    fieldWithPath("targetOptionGroupId").description("지정 옵션그룹 아이디")
                    )));
    }

    @Test
    @DisplayName("옵션 그룹 리스트 조회")
    void getOptionGroups() throws Exception{
        SelectOptionGroupResponseDto selectOptionGroupResponseDto1
            = new SelectOptionGroupResponseDto(1L, 1L, "옵션그룹1", 0, 0, false);
        SelectOptionGroupResponseDto selectOptionGroupResponseDto2
            = new SelectOptionGroupResponseDto(2L, 1L, "옵션그룹2", 0, 0, false);
        SelectOptionGroupResponseDto selectOptionGroupResponseDto3
            = new SelectOptionGroupResponseDto(3L, 1L, "옵션그룹3", 0, 0, false);
        SelectOptionGroupResponseDto selectOptionGroupResponseDto4
            = new SelectOptionGroupResponseDto(4L, 1L, "옵션그룹4", 0, 0, false);

        List<SelectOptionGroupResponseDto> SelectOptionGroupResponseDtoList
            = List.of(selectOptionGroupResponseDto1, selectOptionGroupResponseDto2, selectOptionGroupResponseDto3, selectOptionGroupResponseDto4);

        when(optionGroupService.selectOptionGroups(any())).thenReturn(SelectOptionGroupResponseDtoList);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/option-group", 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(1L))
            .andExpect(jsonPath("$.[0].storeId").value(1L))
            .andExpect(jsonPath("$.[0].name").value("옵션그룹1"))
            .andExpect(jsonPath("$.[0].minSelectCount").value(0))
            .andExpect(jsonPath("$.[0].maxSelectCount").value(0))
            .andExpect(jsonPath("$.[0].isDeleted").value(false))
            .andExpect(jsonPath("$.[1].id").value(2L))
            .andExpect(jsonPath("$.[1].storeId").value(1L))
            .andExpect(jsonPath("$.[1].name").value("옵션그룹2"))
            .andExpect(jsonPath("$.[1].minSelectCount").value(0))
            .andExpect(jsonPath("$.[1].maxSelectCount").value(0))
            .andExpect(jsonPath("$.[1].isDeleted").value(false))
            .andExpect(jsonPath("$.[2].id").value(3L))
            .andExpect(jsonPath("$.[2].storeId").value(1L))
            .andExpect(jsonPath("$.[2].name").value("옵션그룹3"))
            .andExpect(jsonPath("$.[2].minSelectCount").value(0))
            .andExpect(jsonPath("$.[2].maxSelectCount").value(0))
            .andExpect(jsonPath("$.[2].isDeleted").value(false))
            .andExpect(jsonPath("$.[3].id").value(4L))
            .andExpect(jsonPath("$.[3].storeId").value(1L))
            .andExpect(jsonPath("$.[3].name").value("옵션그룹4"))
            .andExpect(jsonPath("$.[3].minSelectCount").value(0))
            .andExpect(jsonPath("$.[3].maxSelectCount").value(0))
            .andExpect(jsonPath("$.[3].isDeleted").value(false))
            .andDo(MockMvcRestDocumentationWrapper.document("selectOptionGroups",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectOptionGroups.Response")),
                responseFields(
                    fieldWithPath("[].id").description("옵션그룹 아이디"),
                    fieldWithPath("[].storeId").description("매장 아이디"),
                    fieldWithPath("[].name").description("옵션그룹 이름"),
                    fieldWithPath("[].minSelectCount").description("옵션 최소 선택 수"),
                    fieldWithPath("[].maxSelectCount").description("옵션 최대 선택 수"),
                    fieldWithPath("[].isDeleted").description("삭제 여부")
                )));
    }

    @Test
    @DisplayName("옵션 그룹 단건 조회")
    void getOptionGroup() throws Exception{
        SelectOptionGroupResponseDto selectOptionGroupResponseDto
            = new SelectOptionGroupResponseDto(1L, 1L, "옵션그룹1", 0, 0, false);

        when(optionGroupService.selectOptionGroup(any())).thenReturn(selectOptionGroupResponseDto);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/option-group/{optionGroupId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.storeId").value(1L))
            .andExpect(jsonPath("$.name").value("옵션그룹1"))
            .andExpect(jsonPath("$.minSelectCount").value(0))
            .andExpect(jsonPath("$.maxSelectCount").value(0))
            .andExpect(jsonPath("$.isDeleted").value(false))
            .andDo(MockMvcRestDocumentationWrapper.document("selectOptionGroup",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectOptionGroup.Response")),
                responseFields(
                    fieldWithPath("id").description("옵션그룹 아이디"),
                    fieldWithPath("storeId").description("매장 아이디"),
                    fieldWithPath("name").description("옵션그룹 이름"),
                    fieldWithPath("minSelectCount").description("옵션 최소 선택 수"),
                    fieldWithPath("maxSelectCount").description("옵션 최대 선택 수"),
                    fieldWithPath("isDeleted").description("삭제 여부")
                )));
    }

    @Test
    @DisplayName("옵션 그룹 삭제")
    void deleteOptionGroup() throws Exception {
        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .delete("/api/stores/{storeId}/option-group/{optionGroupId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("removeOptionGroup",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("optionGroupId").description("옵션 그룹 아이디"))
                    .requestSchema(Schema.schema("deleteOptionGroup.Request"))));
    }
}
