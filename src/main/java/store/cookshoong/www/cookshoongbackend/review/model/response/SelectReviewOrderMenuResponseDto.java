package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 먹은 메뉴에 대한 이름을 확인하기 위한 dto.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
public class SelectReviewOrderMenuResponseDto implements Comparable<SelectReviewOrderMenuResponseDto>, Remove {
    private final Long menuId;
    private final String menuName;

    @QueryProjection
    public SelectReviewOrderMenuResponseDto(Long menuId, String menuName) {
        this.menuId = menuId;
        this.menuName = menuName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SelectReviewOrderMenuResponseDto that = (SelectReviewOrderMenuResponseDto) o;
        return Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }

    @Override
    public int compareTo(@NotNull SelectReviewOrderMenuResponseDto other) {
        return menuId.compareTo(other.getMenuId());
    }

    @Override
    public boolean isNull() {
        return Objects.isNull(menuId);
    }
}
