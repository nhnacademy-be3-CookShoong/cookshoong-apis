package store.cookshoong.www.cookshoongbackend.point.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;

/**
 * 포인트 사유 주문 entity.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("ORDER")
@Table(name = "point_reason_orders")
public class PointReasonOrder extends PointReason {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    Order order;

    public PointReasonOrder(Order order, String explain) {
        super(explain);
        this.order = order;
    }
}
