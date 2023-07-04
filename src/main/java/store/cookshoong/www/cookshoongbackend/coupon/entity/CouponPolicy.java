package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.time.LocalTime;
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
 * 쿠폰 정책 entity.
 *
 * @author eora21
 * @since 2023/07/04
 */

@Getter
@Entity
@Table(name = "coupon_policy")
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_policy_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_type_id", nullable = false)
    private CouponType couponType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_usage_id", nullable = false)
    private CouponUsage couponUsage;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "expiration_time", nullable = false)
    private LocalTime expirationTime;

}
