package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자가 등록한 리뷰 이미지들 조회.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
public class SelectReviewImageResponseDto implements NullAwareComparable<SelectReviewImageResponseDto> {
    @Setter
    private String savedName;
    private final String locationType;
    private final String domainName;

    /**
     * Instantiates a new Select review image response dto.
     *
     * @param savedName    the saved name
     * @param locationType the location type
     * @param domainName   the domain name
     */
    @QueryProjection
    public SelectReviewImageResponseDto(String savedName, String locationType, String domainName) {
        this.savedName = savedName;
        this.locationType = locationType;
        this.domainName = domainName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SelectReviewImageResponseDto that = (SelectReviewImageResponseDto) o;
        return Objects.equals(savedName, that.savedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(savedName);
    }

    @Override
    public int compareTo(@NotNull SelectReviewImageResponseDto other) {
        return savedName.compareTo(other.getSavedName());
    }

    @Override
    public boolean isNull() {
        return Objects.isNull(savedName);
    }
}
