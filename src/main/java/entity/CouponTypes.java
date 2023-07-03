package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "coupon_types")
public class CouponTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "coupon_type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponTypeId;

}
