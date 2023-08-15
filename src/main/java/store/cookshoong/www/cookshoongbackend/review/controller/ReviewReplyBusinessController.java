package store.cookshoong.www.cookshoongbackend.review.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewReplyValidException;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.request.ModifyBusinessReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.review.service.ReviewReplyService;
import store.cookshoong.www.cookshoongbackend.review.service.ReviewService;

/**
 * 매장에 대란 리뷰 Controller.
 *
 * @author jeongjewan
 * @since 2023.08.15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business/review")
public class ReviewReplyBusinessController {

    private final ReviewReplyService reviewReplyService;
    private final ReviewService reviewService;

    /**
     * 회원 리뷰에 답글을 다는 메서드.
     *
     * @param requestDto        회원 리뷰 답글 내용
     * @return                  상태코드 201(CREATED)와 함께 응답을 반환
     */
    @PostMapping("{reviewId}/review-reply")
    private ResponseEntity<Void> postCreateReviewReply(@PathVariable Long reviewId,
                                                       @RequestBody @Valid CreateBusinessReviewRequestDto requestDto,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ReviewReplyValidException(bindingResult);
        }

        reviewReplyService.createBusinessReviewReply(reviewId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 회원 리뷰에 답글을 수정하는 메서드.
     *
     * @param reviewReplyId     리뷰 답글 아이디
     * @param requestDto        리뷰 답글 내용
     * @return                  상태코드 200(Ok)와 함께 응답을 반환
     */
    @PatchMapping("/review-reply/{reviewReplyId}")
    public ResponseEntity<Void> patchModifyReviewReply(@PathVariable Long reviewReplyId,
                                                        @RequestBody @Valid ModifyBusinessReviewRequestDto requestDto,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ReviewReplyValidException(bindingResult);
        }

        reviewReplyService.updateBusinessReviewReply(reviewReplyId, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 사장님 리뷰를 조회하는 메서드.
     *
     * @param storeId       매장 아이디
     * @param pageable      페이지 처리
     * @return              상태코드 200(Ok)와 함께 응답을 반환 & 매장에 대한 모든 리뷰를 반환
     */
    @GetMapping("/review/{storeId}")
    private ResponseEntity<Page<SelectReviewStoreResponseDto>> getBusinessReviewByStore(@PathVariable Long storeId,
                                                                                Pageable pageable) {


        return ResponseEntity.ok(reviewService.selectReviewByStore(storeId, pageable));
    }

    /**
     * 사장님이 회원 리뷰에 답글을 삭제하는 메서드.
     *
     * @param reviewReplyId     리뷰 답글 아이디
     * @return                  상태코드 200(Ok)와 함께 응답을 반환
     */
    @DeleteMapping("/review-reply/{reviewReplyId}")
    public ResponseEntity<Void> deleteReviewReply(@PathVariable Long reviewReplyId) {

        reviewReplyService.deleteBusinessReviewReply(reviewReplyId);

        return ResponseEntity.ok().build();
    }
}
