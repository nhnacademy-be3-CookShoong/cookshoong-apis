package store.cookshoong.www.cookshoongbackend.order.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;

/**
 * 옵션-주문상세 엔티티.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "order_detail_menu_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetailMenuOption {
    @EmbeddedId
    private Pk pk;

    @MapsId("optionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @MapsId("orderDetailId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetail orderDetail;

    @Column(name = "now_name")
    private String nowName;

    @Column(name = "now_price")
    private Integer nowPrice;

    /**
     * 매장_카테고리 엔티티 복합키.
     *
     * @author papel
     * @since 2023.07.11
     */
    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {
        private Long optionId;
        private Long orderDetailId;
    }

    /**
     * Instantiates a new Order detail menu option.
     *
     * @param option      the option
     * @param orderDetail the order detail
     * @param nowName     the now name
     * @param nowPrice    the now price
     */
    public OrderDetailMenuOption(Option option, OrderDetail orderDetail, String nowName, Integer nowPrice) {
        this.pk = new Pk(option.getId(), orderDetail.getId());
        this.option = option;
        this.orderDetail = orderDetail;
        this.nowName = nowName;
        this.nowPrice = nowPrice;
    }
}
