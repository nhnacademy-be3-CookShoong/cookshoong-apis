package store.cookshoong.www.cookshoongbackend.review.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사장님이 회원리뷰에 답글을 달 때 필요한 dto.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateBusinessReviewRequestDto {
    @NotBlank
    private String contents;
}
