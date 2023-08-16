package store.cookshoong.www.cookshoongbackend.coupon.entity;

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
    private static final int DAY_OFFSET = 1;

    /**
     * Instantiates a new Coupon policy.
     *
     * @param couponType  the coupon type
     * @param couponUsage the coupon usage
     * @param name        the name
     * @param description the description
     * @param usagePeriod the expiration time
     */
    public CouponPolicy(CouponType couponType, CouponUsage couponUsage, String name, String description,
                        Integer usagePeriod) {
        this.couponType = couponType;
        this.couponUsage = couponUsage;
        this.name = name;
        this.description = description;
        this.usagePeriod = usagePeriod;
        this.deleted = false;
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

    @Column(name = "usage_period", nullable = false)
    private Integer usagePeriod;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    /**
     * 사용 기간 획득 시 개발자의 관점에 맞추기 위해 1을 감소하여 반환하도록 함.
     *
     * @return the usage period
     */
    public Integer getUsagePeriod() {
        return usagePeriod - DAY_OFFSET;
    }

    /**
     * 삭제 메서드.
     */
    public void delete() {
        this.deleted = true;
    }

    public void hide() {
        this.hidden = true;
    }
}
