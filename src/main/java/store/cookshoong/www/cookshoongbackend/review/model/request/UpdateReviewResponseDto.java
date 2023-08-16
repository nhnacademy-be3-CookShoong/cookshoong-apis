package store.cookshoong.www.cookshoongbackend.review.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 수정시 필요한 dto.
 *
 * @author seungyeon
 * @since 2023.08.16
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateReviewResponseDto {
    @NotBlank
    private String contents;
}
