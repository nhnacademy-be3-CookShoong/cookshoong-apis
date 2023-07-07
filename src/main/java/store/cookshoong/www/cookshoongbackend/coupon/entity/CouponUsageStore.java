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
import store.cookshoong.www.cookshoongbackend.store.entity.Store;

/**
 * 쿠폰 사용처 가게 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "coupon_usage_store")
@DiscriminatorValue("STORE")
public class CouponUsageStore extends CouponUsage {

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Override
    public String getTypeName() {
        return "STORE";
    }
}
