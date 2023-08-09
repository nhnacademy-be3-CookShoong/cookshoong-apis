package store.cookshoong.www.cookshoongbackend.point.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 사유 entity.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_reason")
@DiscriminatorColumn(name = "sub_type")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PointReason {
    public PointReason(String explain) {
        this.explain = explain;
    }

    @Id
    @Column(name = "point_reason_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "point_reason_explain")
    private String explain;
}
