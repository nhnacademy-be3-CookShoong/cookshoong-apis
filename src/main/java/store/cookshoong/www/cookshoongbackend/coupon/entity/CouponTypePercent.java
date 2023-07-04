package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;

/**
 * 쿠폰 타입 할인률 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "coupon_type_percent")
@DiscriminatorValue("PER")
public class CouponTypePercent extends CouponType {

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "minimum_price")
    private Integer minimumPrice;

    @Column(name = "maximum_price", nullable = false)
    private Integer maximumPrice;

}
