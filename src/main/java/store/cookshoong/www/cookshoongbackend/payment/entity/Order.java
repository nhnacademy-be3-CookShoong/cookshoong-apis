package store.cookshoong.www.cookshoongbackend.payment.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_code", nullable = false, length = 36)
    private String orderCode;

    @Column(name = "ordered_at", nullable = false)
    private Instant orderedAt;

    @Column(name = "memo", length = 100)
    private String memo;

}
