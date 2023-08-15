package store.cookshoong.www.cookshoongbackend.account.entity;

import java.io.Serializable;
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
import store.cookshoong.www.cookshoongbackend.coupon.entity.CouponPolicy;


@Getter
@Entity
@Table(name = "ranks_has_coupon_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RanksHasCouponPolicy {
    @EmbeddedId
    private Pk pk;

    @MapsId("rankCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rank_code", nullable = false)
    private Rank rankCode;

    @MapsId("couponPolicyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    private CouponPolicy couponPolicy;

    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {
        private String rankCode;
        private Long couponPolicyId;
    }
}
