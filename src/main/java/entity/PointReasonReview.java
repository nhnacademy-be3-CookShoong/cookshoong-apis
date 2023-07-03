package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "point_reason_review")
public class PointReasonReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "point_reason_id", nullable = false)
    private Long pointReasonId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

}
