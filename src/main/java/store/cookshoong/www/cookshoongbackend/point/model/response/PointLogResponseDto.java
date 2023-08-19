package store.cookshoong.www.cookshoongbackend.point.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.point.entity.PointReason;

/**
 * 포인트 로그 응답 dto.
 *
 * @author eora21 (김주호)
 * @since 2023.08.08
 */
@Getter
public class PointLogResponseDto {
    private final String reason;
    private final int pointMovement;
    private final LocalDateTime pointAt;

    /**
     * Instantiates a new Point log response dto.
     *
     * @param pointReason   the point reason
     * @param pointMovement the point movement
     * @param pointAt       the point at
     */
    @QueryProjection
    public PointLogResponseDto(PointReason pointReason, int pointMovement, LocalDateTime pointAt) {
        this.reason = pointReason.getExplain();
        this.pointMovement = pointMovement;
        this.pointAt = pointAt;
    }
}
