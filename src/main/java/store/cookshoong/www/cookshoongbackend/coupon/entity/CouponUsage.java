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
 * 쿠폰 사용처 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "coupon_usage")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "sub_type")
public abstract class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_usage_id", nullable = false)
    private Long id;

    /**
     * 쿠폰 제한 개수를 정의하는 메서드.
     *
     * @return the optional int
     */
    public abstract OptionalInt limitCount();
}
