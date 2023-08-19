package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

/**
 * 한 매장 대한 리뷰 조회 dto.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@ToString
@Getter
public class SelectReviewStoreResponseDto {

    private final Long accountId;
    private final String nickname;
    private final SelectReviewResponseDto selectReviewResponse;

    /**
     * 매장에 대한 리뷰를 보여줄 생성자.
     *
     * @param accountId            리뷰 적은 회원 아이디
     * @param nickname             사용자 닉네임
     * @param selectReviewResponse 매장에 보여줄 정보
     */
    @QueryProjection
    public SelectReviewStoreResponseDto(Long accountId, String nickname, SelectReviewResponseDto selectReviewResponse) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.selectReviewResponse = selectReviewResponse;
    }
}
