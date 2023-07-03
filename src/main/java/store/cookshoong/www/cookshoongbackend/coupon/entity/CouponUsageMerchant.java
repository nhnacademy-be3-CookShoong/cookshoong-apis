package store.cookshoong.www.cookshoongbackend.coupon.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "coupon_usage_merchant")
public class CouponUsageMerchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "coupon_usage_id", nullable = false)
    private Long couponUsageId;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

}
