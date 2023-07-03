package store.cookshoong.www.cookshoongbackend.coupon.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Data
@Entity
@Table(name = "coupon_policy")
public class CouponPolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_policy_id", nullable = false)
    private Long couponPolicyId;

    @Column(name = "coupon_type_id", nullable = false)
    private Integer couponTypeId;

    @Column(name = "coupon_usage_id", nullable = false)
    private Long couponUsageId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "expiration_time", nullable = false)
    private Time expirationTime;

}
