package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.util.OptionalInt;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Getter;

/**
 * 쿠폰 타입 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "coupon_types")
@DiscriminatorColumn(name = "sub_type")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CouponType {

    @Id
    @Column(name = "coupon_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public abstract Integer getMinimumOrderPrice();
}
