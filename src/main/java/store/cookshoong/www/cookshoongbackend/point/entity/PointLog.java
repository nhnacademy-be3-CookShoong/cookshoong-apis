package store.cookshoong.www.cookshoongbackend.point.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;

/**
 * 포인트 로그 entity.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_logs")
public class PointLog {

    @Id
    @Column(name = "point_logs_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_reason_id")
    private PointReason pointReason;

    @Column(name = "point_movement")
    private int pointMovement;

    @Column(name = "point_at")
    private LocalDateTime pointAt;

    /**
     * Instantiates a new Point log.
     *
     * @param account       the account
     * @param pointReason   the point reason
     * @param pointMovement the point movement
     */
    public PointLog(Account account, PointReason pointReason, int pointMovement) {
        this.account = account;
        this.pointReason = pointReason;
        this.pointMovement = pointMovement;
        this.pointAt = LocalDateTime.now();
    }
}
