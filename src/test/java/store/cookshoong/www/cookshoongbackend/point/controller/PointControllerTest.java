package store.cookshoong.www.cookshoongbackend.point.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReason;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointLogResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointResponseDto;
import store.cookshoong.www.cookshoongbackend.point.service.PointService;

@AutoConfigureRestDocs
@WebMvcTest(PointController.class)
class PointControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    PointService pointService;

    List<PointLogResponseDto> pointLogResponses;

    @BeforeEach
    void beforeEach() {
        PointReason firstReason = mock(PointReason.class);
        when(firstReason.getExplain()).thenReturn("회원가입");

        PointReason secondReason = mock(PointReason.class);
        when(secondReason.getExplain()).thenReturn("주문 사용");

        PointReason thirdReason = mock(PointReason.class);
        when(thirdReason.getExplain()).thenReturn("주문 적립");

        pointLogResponses = List.of(
            new PointLogResponseDto(thirdReason, 4_000, LocalDateTime.now().minusMinutes(30)),
            new PointLogResponseDto(secondReason, 4_000, LocalDateTime.now().minusHours(1)),
            new PointLogResponseDto(firstReason, 4_000, LocalDateTime.now().minusDays(1))
        );
    }

    @Test
    @DisplayName("포인트 확인")
    void getMyPoint() throws Exception {
        int totalPoint = 0;
        for (PointLogResponseDto pointLogResponse : pointLogResponses) {
            totalPoint += pointLogResponse.getPointMovement();
        }

        when(pointService.selectSumPoint(anyLong()))
            .thenReturn(new PointResponseDto(totalPoint));

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/point/{accountId}", 32L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getMyPoint",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("getMyPoint.Response")),
                pathParameters(parameterWithName("accountId").description("사용자 id")),
                responseFields(
                    fieldWithPath("point").description("현재 포인트")
                )
            ));
    }

    @Test
    @DisplayName("포인트 로그 확인")
    void getMyPointLog() throws Exception {
        when(pointService.selectAllPointLog(anyLong(), any(Pageable.class)))
            .thenAnswer(invocation -> new PageImpl<>(pointLogResponses, invocation.getArgument(1),
                pointLogResponses.size()));

        RequestBuilder request = RestDocumentationRequestBuilders
            .get("/api/point/{accountId}/log", 32L)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("getMyPointLog",
                ResourceSnippetParameters.builder()
                    .responseSchema(Schema.schema("getMyPointLog.Response")),
                pathParameters(parameterWithName("accountId").description("사용자 id")),
                requestParameters(
                    parameterWithName("page").optional().description("페이지 번호"),
                    parameterWithName("size").optional().description("한 페이지에 몇 개의 데이터를 불러올 것인지"),
                    parameterWithName("sort").optional().description("페이징 정렬조건")
                ),
                responseFields(
                    fieldWithPath("content[].reason").description("포인트 적립/사용 사유"),
                    fieldWithPath("content[].pointMovement").description("포인트 적립/사용량"),
                    fieldWithPath("content[].pointAt").description("적립시간"),
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
    }
}
