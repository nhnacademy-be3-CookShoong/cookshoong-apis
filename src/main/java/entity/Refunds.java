package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "refunds")
public class Refunds implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "refund_code", nullable = false)
    private String refundCode;

    /**
     * AI
     */
    @Column(name = "refund_type_id", nullable = false)
    private Long refundTypeId;

    @Column(name = "charge_code", nullable = false)
    private String chargeCode;

    @Column(name = "refunded_at", nullable = false)
    private Date refundedAt;

    @Column(name = "refund_amount", nullable = false)
    private Long refundAmount;

}
