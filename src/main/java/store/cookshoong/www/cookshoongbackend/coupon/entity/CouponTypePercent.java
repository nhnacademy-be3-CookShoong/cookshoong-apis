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
 * 쿠폰 타입 할인률 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "coupon_type_percent")
@DiscriminatorValue("PERCENT")
public class CouponTypePercent extends CouponType {

    @Column(name = "rate", nullable = false)
    private Integer rate;

    @Column(name = "maximum_discount_amount", nullable = false)
    private Integer maximumDiscountAmount;

    @Column(name = "minimum_order_price")
    private Integer minimumOrderPrice;

    @Override
    public int getDiscountPrice(int totalPrice) {
        int discount = Math.min(totalPrice / 100 * rate, getMaximumDiscountAmount());
        int discountPrice = totalPrice - discount;
        return Math.max(0, discountPrice);
    }
}
