package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "point_logs")
public class PointLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "point_logs_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointLogsId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "point_reason_id", nullable = false)
    private Long pointReasonId;

    @Column(name = "point_movement", nullable = false)
    private Integer pointMovement;

    @Column(name = "point_at", nullable = false)
    private Date pointAt;

}
