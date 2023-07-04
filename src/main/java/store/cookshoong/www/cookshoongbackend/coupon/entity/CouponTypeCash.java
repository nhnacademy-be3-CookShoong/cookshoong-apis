package store.cookshoong.www.cookshoongbackend.coupon.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;

/**
 * 쿠폰 타입 금액 entity.
 *
 * @author eora21
 * @since 2023/07/04
 */

@Getter
@Entity
@Table(name = "coupon_type_cash")
@DiscriminatorValue("CASH")
public class CouponTypeCash extends CouponType {

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "minimum_price")
    private Integer minimumPrice;
}
