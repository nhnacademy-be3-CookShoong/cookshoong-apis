package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;

/**
 * 쿠폰 사용처 가맹점 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "coupon_usage_merchant")
@DiscriminatorValue("MERCHANT")
public class CouponUsageMerchant extends CouponUsage {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<IssueMethod> issueMethods() {
        return Collections.unmodifiableSet(EnumSet.of(IssueMethod.BULK, IssueMethod.EVENT));
    }
}
