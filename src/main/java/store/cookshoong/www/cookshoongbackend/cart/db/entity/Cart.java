package store.cookshoong.www.cookshoongbackend.cart.db.entity;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * Redis 장바구니에 저장된 정보를 Db 에 저장하는 Cart Entity.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "cart_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE)
    private List<CartDetail> cartDetails;

    /**
     * 회원과 매장에 대한 생성자.
     *
     * @param account       회원
     * @param store         매장
     */
    public Cart(Account account, Store store) {
        this.account = account;
        this.store = store;
    }
}
