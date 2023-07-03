package store.cookshoong.www.cookshoongbackend.order;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "order_status")
public class OrderStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "order_status_code", nullable = false)
    private String orderStatusCode;

    @Column(name = "description", nullable = false)
    private String description;

}
