package entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "point_reason_orders")
public class PointReasonOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "point_reason_id", nullable = false)
    private Long pointReasonId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

}
