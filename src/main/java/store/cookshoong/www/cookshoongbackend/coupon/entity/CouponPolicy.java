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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쿠폰 정책 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_policy")
public class CouponPolicy {

    /**
     * Instantiates a new Coupon policy.
     *
     * @param couponType     the coupon type
     * @param couponUsage    the coupon usage
     * @param name           the name
     * @param description    the description
     * @param expirationTime the expiration time
     */
    public CouponPolicy(CouponType couponType, CouponUsage couponUsage, String name, String description,
                        LocalTime expirationTime) {
        this.couponType = couponType;
        this.couponUsage = couponUsage;
        this.name = name;
        this.description = description;
        this.expirationTime = expirationTime;
    }

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
