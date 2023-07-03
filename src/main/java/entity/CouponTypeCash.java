package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "coupon_type_cash")
public class CouponTypeCash implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "coupon_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponTypeId;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "minimum_price")
    private Integer minimumPrice;

}
