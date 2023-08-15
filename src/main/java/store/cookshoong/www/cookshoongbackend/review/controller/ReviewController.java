package store.cookshoong.www.cookshoongbackend.review.controller;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.review.exception.ReviewValidException;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.service.ReviewService;

/**
 * 리뷰 등록, 수정, 조회 등 컨트롤러.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/{accountId}")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 등록에 대한 컨트롤러.
     *
     * @param accountId     the account id
     * @param requestDto    the request dto
     * @param bindingResult the binding result
     * @param images        the images
     * @param storedAt      the stored at
     * @return the response entity
     * @throws IOException the io exception
     */
    @PostMapping("/review")
    public ResponseEntity<Void> postReview(@PathVariable("accountId") Long accountId,
                                           @RequestPart("requestDto") @Valid CreateReviewRequestDto requestDto,
                                           BindingResult bindingResult,
                                           @RequestPart(value = "reviewImage", required = false) List<MultipartFile> images,
                                           @RequestParam("storedAt") String storedAt) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new ReviewValidException(bindingResult);
        }
        reviewService.createReview(accountId, requestDto, images, storedAt);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 본인이 작성한 리뷰 조회 컨트롤러 : 리뷰 내용, 매장, 메뉴, 사장님 답글.. 등을 볼 수 있음.
     *
     * @param accountId the account id
     * @param pageable  the pageable
     * @return the review by account
     */
    @GetMapping("/review")
    public ResponseEntity<Page<SelectReviewResponseDto>> getReviewByAccount(@PathVariable("accountId") Long accountId,
                                                                            Pageable pageable) {
        return ResponseEntity
            .ok(reviewService.selectReviewByAccount(accountId, pageable));
    }



}
