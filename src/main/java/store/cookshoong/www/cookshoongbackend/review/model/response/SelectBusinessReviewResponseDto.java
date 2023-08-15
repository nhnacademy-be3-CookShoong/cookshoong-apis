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
    private final Long reviewReplyId;
    private final String contents;
    private final LocalDateTime writtenAt;

    /**
     * 사장님이 단 답글에 대한 쿼리.
     *
     * @param reviewReplyId     리뷰 답글 아이디
     * @param contents          리뷰 답글 내용
     * @param writtenAt         리뷰 답글 작성한 시간
     */
    @QueryProjection
    public SelectBusinessReviewResponseDto(Long reviewReplyId, String contents, LocalDateTime writtenAt){

        this.reviewReplyId = reviewReplyId;
        this.contents = contents;
        this.writtenAt = writtenAt;
    }
}
