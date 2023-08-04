package store.cookshoong.www.cookshoongbackend.order.entity;

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
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;

/**
 * 주문 상세 엔티티.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_code", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "count")
    private Integer count;

    @Column(name = "now_name")
    private String nowName;

    @Column(name = "now_cost", nullable = false)
    private Integer nowCost;

    /**
     * Instantiates a new Order detail.
     *
     * @param order   the order
     * @param menu    the menu
     * @param count   the count
     * @param nowName the now name
     * @param nowCost the now cost
     */
    public OrderDetail(Order order, Menu menu, Integer count, String nowName, Integer nowCost) {
        this.order = order;
        this.menu = menu;
        this.count = count;
        this.nowName = nowName;
        this.nowCost = nowCost;
    }
}
