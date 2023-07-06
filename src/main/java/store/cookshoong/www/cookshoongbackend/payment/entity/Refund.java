package store.cookshoong.www.cookshoongbackend.payment.entity;

import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "refunds")
public class Refund {
    @Id
    @Size(max = 36)
    @Column(name = "refund_code", nullable = false, length = 36)
    private String refundCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refund_type_id", nullable = false)
    private RefundType refundType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "charge_code", nullable = false)
    private Charge chargeCode;

    @Column(name = "refunded_at", nullable = false)
    private Date refundedAt;

    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

}
