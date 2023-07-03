package store.cookshoong.www.cookshoongbackend.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "order_details")
public class OrderDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id", nullable = false)
    private Long orderDetailId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "count", nullable = false)
    private Long count;

    @Column(name = "now_cost", nullable = false)
    private Integer nowCost;

}
