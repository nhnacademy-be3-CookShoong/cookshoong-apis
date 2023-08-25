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
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionGroupService;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionService;

@AutoConfigureRestDocs
@WebMvcTest(OptionController.class)
class OptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    OptionService optionService;

    @MockBean
    OptionGroupService optionGroupService;


    @Test
    @DisplayName("옵션 등록")
    void postOption() throws Exception{
        CreateOptionRequestDto createOptionRequestDto
            = ReflectionUtils.newInstance(CreateOptionRequestDto.class);
        ReflectionTestUtils.setField(createOptionRequestDto, "name", "옵션1");
        ReflectionTestUtils.setField(createOptionRequestDto, "price", 500);
        ReflectionTestUtils.setField(createOptionRequestDto, "optionGroup", 1L);
        ReflectionTestUtils.setField(createOptionRequestDto, "targetOptionId", null);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .post("/api/stores/{storeId}/option", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOptionRequestDto));
        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(MockMvcRestDocumentationWrapper.document("createOption",
                ResourceSnippetParameters.builder()
                    .requestSchema(Schema.schema("createOption.Request")),
                requestFields(
                    fieldWithPath("name").description("옵션 이름"),
                    fieldWithPath("price").description("옵션 가격"),
                    fieldWithPath("optionGroup").description("옵션 그룹"),
                    fieldWithPath("targetOptionId").description("지정 옵션 아이디")
                    )));
    }

    @Test
    @DisplayName("옵션 리스트 조회")
    void getOptions() throws Exception{
        SelectOptionResponseDto selectOptionResponseDto1
            = new SelectOptionResponseDto(1L, 1L, "옵션1", 500, false, 0);
        SelectOptionResponseDto selectOptionResponseDto2
            = new SelectOptionResponseDto(2L, 1L, "옵션2", 500, false, 0);
        SelectOptionResponseDto selectOptionResponseDto3
            = new SelectOptionResponseDto(3L, 1L, "옵션3", 500, false, 0);
        SelectOptionResponseDto selectOptionResponseDto4
            = new SelectOptionResponseDto(4L, 1L, "옵션4", 500, false, 0);

        List<SelectOptionResponseDto> SelectOptionResponseDtoList
            = List.of(selectOptionResponseDto1, selectOptionResponseDto2, selectOptionResponseDto3, selectOptionResponseDto4);

        when(optionService.selectOptions(any())).thenReturn(SelectOptionResponseDtoList);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/option", 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(1L))
            .andExpect(jsonPath("$.[0].optionGroupId").value(1L))
            .andExpect(jsonPath("$.[0].name").value("옵션1"))
            .andExpect(jsonPath("$.[0].price").value(500))
            .andExpect(jsonPath("$.[0].isDeleted").value(false))
            .andExpect(jsonPath("$.[0].optionSequence").value(0))
            .andExpect(jsonPath("$.[1].id").value(2L))
            .andExpect(jsonPath("$.[1].optionGroupId").value(1L))
            .andExpect(jsonPath("$.[1].name").value("옵션2"))
            .andExpect(jsonPath("$.[1].price").value(500))
            .andExpect(jsonPath("$.[1].isDeleted").value(false))
            .andExpect(jsonPath("$.[1].optionSequence").value(0))
            .andExpect(jsonPath("$.[2].id").value(3L))
            .andExpect(jsonPath("$.[2].optionGroupId").value(1L))
            .andExpect(jsonPath("$.[2].name").value("옵션3"))
            .andExpect(jsonPath("$.[2].price").value(500))
            .andExpect(jsonPath("$.[2].isDeleted").value(false))
            .andExpect(jsonPath("$.[2].optionSequence").value(0))
            .andExpect(jsonPath("$.[3].id").value(4L))
            .andExpect(jsonPath("$.[3].optionGroupId").value(1L))
            .andExpect(jsonPath("$.[3].name").value("옵션4"))
            .andExpect(jsonPath("$.[3].price").value(500))
            .andExpect(jsonPath("$.[3].isDeleted").value(false))
            .andExpect(jsonPath("$.[3].optionSequence").value(0))
            .andDo(MockMvcRestDocumentationWrapper.document("selectOptions",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectOptions.Response")),
                responseFields(
                    fieldWithPath("[].id").description("옵션 아이디"),
                    fieldWithPath("[].optionGroupId").description("옵션그룹 아이디"),
                    fieldWithPath("[].name").description("옵션 이름"),
                    fieldWithPath("[].price").description("옵션 가격"),
                    fieldWithPath("[].isDeleted").description("삭제 여부"),
                    fieldWithPath("[].optionSequence").description("옵션 순서")
                )));
    }

    @Test
    @DisplayName("옵션 단건 조회")
    void getOption() throws Exception{
        SelectOptionResponseDto selectOptionResponseDto1
            = new SelectOptionResponseDto(1L, 1L, "옵션1", 500, false, 0);

        when(optionService.selectOption(any())).thenReturn(selectOptionResponseDto1);

        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .get("/api/stores/{storeId}/option/{optionId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.optionGroupId").value(1L))
            .andExpect(jsonPath("$.name").value("옵션1"))
            .andExpect(jsonPath("$.price").value(500))
            .andExpect(jsonPath("$.isDeleted").value(false))
            .andExpect(jsonPath("$.optionSequence").value(0))
            .andDo(MockMvcRestDocumentationWrapper.document("selectOption",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("selectOption.Response")),
                responseFields(
                    fieldWithPath("id").description("옵션 아이디"),
                    fieldWithPath("optionGroupId").description("옵션그룹 아이디"),
                    fieldWithPath("name").description("옵션 이름"),
                    fieldWithPath("price").description("옵션 가격"),
                    fieldWithPath("isDeleted").description("삭제 여부"),
                    fieldWithPath("optionSequence").description("옵션 순서")
                )));
    }

    @Test
    @DisplayName("옵션 삭제")
    void deleteOption() throws Exception {
        RequestBuilder requestBuilder = RestDocumentationRequestBuilders
            .delete("/api/stores/{storeId}/option/{optionId}", 1L, 1L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("removeOption",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("optionId").description("옵션 아이디"))
                    .requestSchema(Schema.schema("deleteOption.Request"))));
    }
}
