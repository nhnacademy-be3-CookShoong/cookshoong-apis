package store.cookshoong.www.cookshoongbackend.payment.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "refund_code", columnDefinition = "BINARY(16)", nullable = false)
    private UUID code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refund_type_code", nullable = false)
    private RefundType refundType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "charge_code", nullable = false)
    private Charge charge;

    @Column(name = "refunded_at", nullable = false)
    private LocalDateTime refundedAt;

    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    /**
     * Refund 에 대한 생성자.
     *
     * @param refundType        환불 타입
     * @param charge            결제
     * @param refundedAt        환불 승인 날짜
     * @param refundAmount      환불 금액
     */
    public Refund(RefundType refundType, Charge charge, LocalDateTime refundedAt, Integer refundAmount) {

        this.refundType = refundType;
        this.charge = charge;
        this.refundedAt = refundedAt;
        this.refundAmount = refundAmount;
    }

}
