package store.cookshoong.www.cookshoongbackend.menu.entity.orderdetail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.payment.entity.Order;

/**
 * 주문 상세 엔티티.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "order_details")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_code", nullable = false)
    private Order orderCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "count")
    private Long count;

    @Column(name = "now_cost", nullable = false)
    private Integer nowCount;
}
