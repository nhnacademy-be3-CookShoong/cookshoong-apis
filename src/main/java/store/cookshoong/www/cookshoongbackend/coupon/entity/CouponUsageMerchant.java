package store.cookshoong.www.cookshoongbackend.coupon.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;

/**
 * 쿠폰 사용처 가맹점 entity.
 *
 * @author eora21
 * @since 2023/07/04
 */

@Getter
@Entity
@Table(name = "coupon_usage_merchant")
@DiscriminatorValue("MERCHANT")
public class CouponUsageMerchant extends CouponUsage {

    // TODO: 가맹점 entity 추가 후 @ManyToOne 작성할 것.
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

}
