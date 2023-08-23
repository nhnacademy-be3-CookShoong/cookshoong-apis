package store.cookshoong.www.cookshoongbackend.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.review.service.ReviewService;

/**
 * 매장에 대란 리뷰 Controller.
 *
 * @author jeongjewan
 * @since 2023.08.15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/{storeId}")
public class ReviewStoreController {

    private final ReviewService reviewService;

    /**
     * 매장 리뷰를 조회하는 메서드.
     *
     * @param storeId       매장 아이디
     * @param pageable      페이지 처리
     * @return              상태코드 200(Ok)와 함께 응답을 반환 & 매장에 대한 모든 리뷰를 반환
     */
    @GetMapping("/review")
    public ResponseEntity<Page<SelectReviewStoreResponseDto>> getReviewByStore(@PathVariable Long storeId,
                                                                                Pageable pageable) {

        return ResponseEntity.ok(reviewService.selectReviewByStore(storeId, pageable));
    }
}
