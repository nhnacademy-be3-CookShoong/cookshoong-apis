package store.cookshoong.www.cookshoongbackend.review.controller;


import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.request.ModifyBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.service.ReviewReplyService;
import store.cookshoong.www.cookshoongbackend.review.service.ReviewService;

/**
 * {설명을 작성해주세요}.
 *
 * @author jeongjewan
 * @since 2023.08.19
 */
@AutoConfigureRestDocs
@WebMvcTest(ReviewReplyBusinessController.class)
class ReviewReplyBusinessControllerTest {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReviewService reviewService;
    @MockBean
    private ReviewReplyService reviewReplyService;

    @Test
    @DisplayName("리뷰 답글 생성")
    void postCreateReviewReply() throws Exception {
        Long reviewId = 1L;
        CreateBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(CreateBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "contents", "랄랄랄");

        mockMvc.perform(post("/api/business/review/{reviewId}/review-reply", reviewId)
                .content(om.writeValueAsString(requestDto))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("post-create-review-reply",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("reviewId").description("리뷰 아이디"))
                    .requestSchema(schema("PostCreateReviewReply")),
                requestFields(
                    fieldWithPath("contents").description("리뷰답글내용")
                )));

        verify(reviewReplyService, times(1)).createBusinessReviewReply(eq(reviewId), any());
    }

    @Test
    @DisplayName("리뷰 답글 생성 실패 - 리뷰 답글이 존재하지 않을 때")
    void postCreateReviewReply_Validation_Exception() throws Exception {
        Long reviewId = 1L;
        CreateBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(CreateBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "contents", null);

        mockMvc.perform(post("/api/business/review/{reviewId}/review-reply", reviewId)
                .content(om.writeValueAsString(requestDto))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 답글 수정")
    void patchModifyReviewReply() throws Exception {
        Long reviewReplyId = 1L;
        ModifyBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(ModifyBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "replyContents", "랄랄랄");

        mockMvc.perform(patch("/api/business/review/review-reply/{reviewReplyId}", reviewReplyId)
                .content(om.writeValueAsString(requestDto))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("path-modify-review-reply",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("reviewReplyId").description("리뷰 답글 아이디"))
                    .requestSchema(schema("PatchModifyReviewReply")),
                requestFields(
                    fieldWithPath("replyContents").description("리뷰답글내용")
                )));

        verify(reviewReplyService, times(1)).updateBusinessReviewReply(eq(reviewReplyId), any());
    }

    @Test
    @DisplayName("리뷰 답글 수정 실패 - 리뷰 답글이 존재하지 않을 때")
    void patchModifyReviewReply_Validation_Exception() throws Exception {
        Long reviewReplyId = 1L;
        ModifyBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(ModifyBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "replyContents", null);

        mockMvc.perform(patch("/api/business/review/review-reply/{reviewReplyId}", reviewReplyId)
                .content(om.writeValueAsString(requestDto))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 답글 삭제")
    void deleteReviewReply() throws Exception {
        Long reviewReplyId = 1L;

        mockMvc.perform(delete("/api/business/review/review-reply/{reviewReplyId}", reviewReplyId)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("review-review-reply",
                ResourceSnippetParameters.builder()
                    .pathParameters(parameterWithName("reviewReplyId").description("리뷰 답글 아이디"))
                    .requestSchema(schema("DeleteReviewReply"))
                ));
    }

}
