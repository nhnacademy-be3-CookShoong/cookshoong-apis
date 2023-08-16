package store.cookshoong.www.cookshoongbackend.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewReply;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewReplyNotFoundException;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.request.ModifyBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.repository.ReviewReplyRepository;
import store.cookshoong.www.cookshoongbackend.review.repository.ReviewRepository;

/**
 * 리뷰 답글에 대한 Service.
 *
 * @author jeongjewan
 * @since 2023.08.15
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 사장님이 회원 리뷰에 답글을 다는 메서드.
     *
     * @param reviewId      리뷰 아이디
     * @param requestDto    리뷰에 대한 답글 내용
     */
    public void createBusinessReviewReply(Long reviewId, CreateBusinessReviewRequestDto requestDto) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);

        ReviewReply reviewReply = new ReviewReply(review, requestDto.getContents());

        reviewReplyRepository.save(reviewReply);
    }

    /**
     * 사장님이 회원 리뷰에 대한 답글을 수정하는 메서드.
     *
     * @param reviewReplyId     리뷰 답글 아이디
     * @param requestDto        리뷰 답글 수정 내용
     */
    public void updateBusinessReviewReply(Long reviewReplyId, ModifyBusinessReviewRequestDto requestDto) {

        ReviewReply reviewReply =
            reviewReplyRepository.findById(reviewReplyId).orElseThrow(ReviewReplyNotFoundException::new);

        reviewReply.updateReviewReplyContent(requestDto);
    }

    /**
     * 사장님이 회원 리뷰를 삭제하는 메서드.
     *
     * @param reviewReplyId     리뷰 답글 아이디
     */
    public void deleteBusinessReviewReply(Long reviewReplyId) {

        reviewReplyRepository.deleteById(reviewReplyId);
    }
}
