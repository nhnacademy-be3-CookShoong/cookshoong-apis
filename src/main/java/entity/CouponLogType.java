package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "coupon_log_type")
public class CouponLogType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_log_type_id", nullable = false)
    private Integer couponLogTypeId;

    @Column(name = "name", nullable = false)
    private String name;

}
