package store.cookshoong.www.cookshoongbackend.order.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 주문 엔티티.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @Column(name = "order_code", columnDefinition = "BINARY(16)", nullable = false)
    private UUID code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_status_code", nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "memo", length = 100)
    private String memo;

    /**
     * 주문 생성자.
     *
     * @param code        the code
     * @param orderStatus the order status
     * @param account     the account
     * @param store       the store
     * @param memo        the memo
     */
    public Order(UUID code, OrderStatus orderStatus, Account account, Store store, String memo) {
        this.code = code;
        this.orderStatus = orderStatus;
        this.account = account;
        this.store = store;
        this.orderedAt = LocalDateTime.now();
        this.memo = memo;
    }
}
