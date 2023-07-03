package store.cookshoong.www.cookshoongbackend.coupon.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "coupon_type_percent")
public class CouponTypePercent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "coupon_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponTypeId;

    @Column(name = "rate", nullable = false)
    private Float rate;

    @Column(name = "minimum_price")
    private Integer minimumPrice;

    @Column(name = "maximum_price", nullable = false)
    private Integer maximumPrice;

}
