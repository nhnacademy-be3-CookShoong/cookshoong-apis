package store.cookshoong.www.cookshoongbackend.coupon.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;

/**
 * 쿠폰 사용처 모두 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "coupon_usage_all")
@DiscriminatorValue("ALL")
public class CouponUsageAll extends CouponUsage {

}
