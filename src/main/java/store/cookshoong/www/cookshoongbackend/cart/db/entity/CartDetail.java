package store.cookshoong.www.cookshoongbackend.cart.db.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;

/**
 * Redis 장바구니에 저장된 정보를 Db 에 저장하는 CartDetail Entity.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "cart_details")
public class CartDetail {

    @Id
    @Column(name = "cart_detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "created_time_millis", nullable = false)
    private Long createdTimeMillis;

    @OneToMany(mappedBy = "cartDetail", cascade = CascadeType.REMOVE)
    private List<CartDetailMenuOption> options;

    /**
     * 장바구니와 매장에 대한 생성자.
     *
     * @param cart              장바구니
     * @param menu              메뉴
     * @param count             메뉴 수량
     * @param createdTimeMillis 장바구니에 담을 시간
     */
    public CartDetail(Cart cart, Menu menu, int count, Long createdTimeMillis) {
        this.cart = cart;
        this.menu = menu;
        this.count = count;
        this.createdTimeMillis = createdTimeMillis;
    }
}
