package store.cookshoong.www.cookshoongbackend.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewReply;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewReplyNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.request.ModifyBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.repository.ReviewReplyRepository;
import store.cookshoong.www.cookshoongbackend.review.repository.ReviewRepository;

/**
 * {설명을 작성해주세요}.
 *
 * @author jeongjewan
 * @since 2023.08.19
 */
@ExtendWith(MockitoExtension.class)
class ReviewReplyServiceTest {

    @InjectMocks
    private ReviewReplyService reviewReplyService;

    @Mock
    private ReviewReplyRepository reviewReplyRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 답글 달기")
    void createBusinessReviewReply_Success() {
        Long reviewId = 1L;
        CreateBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(CreateBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "contents", "랄랄랄");

        Review review = new Review();

        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));

        reviewReplyService.createBusinessReviewReply(reviewId, requestDto);

        verify(reviewReplyRepository, times(1)).save(any(ReviewReply.class));
    }

    @Test
    @DisplayName("리뷰 답글 달기 실패 - 리뷰가 존재하지 않을 때")
    void createBusinessReviewReply_InvalidReview_ThrowsReviewNotFoundException() {
        Long reviewId = 1L;
        CreateBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(CreateBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "contents", "랄랄랄");

        when(reviewRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () ->
            reviewReplyService.createBusinessReviewReply(reviewId, requestDto));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateBusinessReviewReply_ValidData_Success() {
        Long reviewReplyId = 1L;
        ModifyBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(ModifyBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "replyContents", "리뷰 수정");


        ReviewReply reviewReply = new ReviewReply();

        when(reviewReplyRepository.findById(any())).thenReturn(Optional.of(reviewReply));

        reviewReplyService.updateBusinessReviewReply(reviewReplyId, requestDto);

        assertEquals(requestDto.getReplyContents(), reviewReply.getContents());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 리뷰 답글이 존재하지 않을 때")
    void updateBusinessReviewReply_InvalidReviewReply_ThrowsReviewReplyNotFoundException() {
        Long reviewReplyId = 1L;
        ModifyBusinessReviewRequestDto requestDto = ReflectionUtils.newInstance(ModifyBusinessReviewRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "replyContents", "리뷰 수정");

        when(reviewReplyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ReviewReplyNotFoundException.class, () ->
            reviewReplyService.updateBusinessReviewReply(reviewReplyId, requestDto));
    }

    @Test
    void deleteBusinessReviewReply_ValidData_Success() {
        Long reviewReplyId = 1L;

        reviewReplyService.deleteBusinessReviewReply(reviewReplyId);

        verify(reviewReplyRepository, times(1)).deleteById(reviewReplyId);
    }

}
