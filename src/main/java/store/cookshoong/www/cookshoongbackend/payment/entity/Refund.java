package store.cookshoong.www.cookshoongbackend.payment.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 환불에 해당되는 Entity.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "refunds")
public class Refund {
    @Id
    @Column(name = "refund_code", nullable = false, length = 36)
    private String refundCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refund_type_code", nullable = false)
    private RefundType refundType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "charge_code", nullable = false)
    private Charge chargeCode;

    @Column(name = "refunded_at", nullable = false)
    private Date refundedAt;

    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

}
