package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "coupon_logs")
public class CouponLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "coupon_logs_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponLogsId;

    @Column(name = "issue_coupon_code", nullable = false)
    private String issueCouponCode;

    /**
     * AI
     */
    @Column(name = "coupon_log_type_id", nullable = false)
    private Integer couponLogTypeId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "record_date", nullable = false)
    private Date recordDate;

}
