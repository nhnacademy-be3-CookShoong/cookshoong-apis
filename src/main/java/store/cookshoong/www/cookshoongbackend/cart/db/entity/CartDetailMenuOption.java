package store.cookshoong.www.cookshoongbackend.cart.db.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
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
 * Redis 장바구니에 저장된 정보를 Db 에 저장하는 CartDetailMenuOption Entity.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "cart_detail_menu_options")
public class CartDetailMenuOption {

    @EmbeddedId
    private Pk pk;

    @MapsId("cartDetailId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "cart_detail_id")
    private CartDetail cartDetail;

    @MapsId("optionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

    /**
     * 장바구니 상세와 옵션에 대한 다대다 관계.
     */
    @Getter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Pk implements Serializable {

        private Long cartDetailId;
        private Long optionId;
    }
}
