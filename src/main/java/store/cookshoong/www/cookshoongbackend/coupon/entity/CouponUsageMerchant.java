package store.cookshoong.www.cookshoongbackend.coupon.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;

/**
 * 쿠폰 사용처 가맹점 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "coupon_usage_merchant")
@DiscriminatorValue("MERCHANT")
public class CouponUsageMerchant extends CouponUsage {

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Override
    public String getTypeName() {
        return "MERCHANT";
    }
}
