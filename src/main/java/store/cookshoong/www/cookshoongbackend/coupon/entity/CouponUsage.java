package store.cookshoong.www.cookshoongbackend.coupon.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "coupon_usage")
public class CouponUsage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_usage_id", nullable = false)
    private Long couponUsageId;

}
