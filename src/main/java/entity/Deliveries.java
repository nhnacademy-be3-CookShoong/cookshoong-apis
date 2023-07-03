package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "deliveries")
public class Deliveries implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "delivery_code", nullable = false)
    private String deliveryCode;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    @Column(name = "store_address", nullable = false)
    private String storeAddress;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_start_at", nullable = false)
    private Date deliveryStartAt;

    @Column(name = "delivery_end_at")
    private Date deliveryEndAt;

}
