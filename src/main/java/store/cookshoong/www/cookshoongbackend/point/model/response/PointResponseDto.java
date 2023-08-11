package store.cookshoong.www.cookshoongbackend.point.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * 포인트 합계 응답 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.08
 */
@Getter
public class PointResponseDto {
    @Setter
    private Integer point;

    /**
     * Instantiates a new Point response dto.
     *
     * @param point the point
     */
    @QueryProjection
    public PointResponseDto(Integer point) {
        if (Objects.isNull(point)) {
            this.point = 0;
            return;
        }

        this.point = point;
    }
}
