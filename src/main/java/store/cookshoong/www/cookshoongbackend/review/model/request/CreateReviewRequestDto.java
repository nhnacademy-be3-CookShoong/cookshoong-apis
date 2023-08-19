package store.cookshoong.www.cookshoongbackend.review.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 리뷰 작성.
 * 각 주문 내역 중 COMPLETE 상태의 주문은 리뷰를 작성할 수 있음.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateReviewRequestDto {
    @NotBlank
    private String orderCode;
    @NotNull
    private Integer rating;
    @NotBlank
    private String contents;
}
