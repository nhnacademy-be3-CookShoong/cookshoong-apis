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
 * 쿠폰 타입 금액 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DiscriminatorValue("CASH")
@Table(name = "coupon_type_cash")
public class CouponTypeCash extends CouponType {

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "minimum_order_price")
    private Integer minimumOrderPrice;

    @Override
    public int getDiscountAmount(int totalPrice) {
        return getDiscountAmount();
    }
}
