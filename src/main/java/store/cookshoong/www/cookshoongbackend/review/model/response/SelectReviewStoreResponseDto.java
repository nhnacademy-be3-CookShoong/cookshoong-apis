package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
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

    private final String nickname;

    @Setter
    private SelectReviewResponseDto selectReviewResponseDto;

    /**
     * 매장에 대한 리뷰를 보여줄 생성자.
     *
     * @param nickname                  사용자 닉네임
     * @param selectReviewResponseDto   매장에 보여줄 정보
     */
    @QueryProjection
    public SelectReviewStoreResponseDto(String nickname,
                                        SelectReviewResponseDto selectReviewResponseDto) {
        this.nickname = nickname;
        this.selectReviewResponseDto = selectReviewResponseDto;
    }
}
