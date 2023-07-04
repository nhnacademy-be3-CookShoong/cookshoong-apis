package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;

/**
 * 쿠폰 내역 entity.
 *
 * @author eora21
 * @since 2023/07/04
 */

@Getter
@Entity
@Table(name = "coupon_logs")
public class CouponLog {

    @Id
    @Column(name = "coupon_logs_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_coupon_code", nullable = false)
    private IssueCoupon issueCoupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_log_type_id", nullable = false)
    private CouponLogType couponLogType;

    // TODO: 주문 entity 추가 후 @ManyToOne 작성할 것.
    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

}
