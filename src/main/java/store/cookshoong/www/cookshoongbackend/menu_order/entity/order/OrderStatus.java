package store.cookshoong.www.cookshoongbackend.menu_order.entity.order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상태 엔티티.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order_status")
public class OrderStatus {
    @Id
    @Column(name = "order_status_code", nullable = false, length = 10)
    private String orderStatusCode;

    @Column(name = "description", nullable = false, length = 40)
    private String description;

}
