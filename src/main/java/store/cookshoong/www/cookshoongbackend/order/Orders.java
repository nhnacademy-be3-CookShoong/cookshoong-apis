package store.cookshoong.www.cookshoongbackend.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "order_status_code", nullable = false)
    private String orderStatusCode;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "ordered_at", nullable = false)
    private Date orderedAt;

    @Column(name = "memo")
    private String memo;

}
