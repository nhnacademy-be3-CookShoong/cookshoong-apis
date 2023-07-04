package store.cookshoong.www.cookshoongbackend.coupon.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    // TODO: 매장 entity 추가 후 @ManyToOne 작성할 것.
    @Column(name = "store_id", nullable = false)
    private Long storeId;

}
