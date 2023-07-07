package store.cookshoong.www.cookshoongbackend.coupon.entity;

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
import store.cookshoong.www.cookshoongbackend.payment.entity.Order;

/**
 * 쿠폰 내역 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code", nullable = false)
    private Order order;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

}
