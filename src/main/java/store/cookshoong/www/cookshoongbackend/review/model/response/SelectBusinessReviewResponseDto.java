package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 사업자 리뷰 조회 dto.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
public class SelectBusinessReviewResponseDto {
    private final String contents;
    private final LocalDateTime writtenAt;

    @QueryProjection
    public SelectBusinessReviewResponseDto(String contents, LocalDateTime writtenAt){
        this.contents = contents;
        this.writtenAt = writtenAt;
    }
}
