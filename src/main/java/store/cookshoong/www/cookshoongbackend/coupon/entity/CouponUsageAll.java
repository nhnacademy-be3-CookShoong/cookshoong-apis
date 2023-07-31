package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;

/**
 * 쿠폰 사용처 모두 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "coupon_usage_all")
@DiscriminatorValue("ALL")
public class CouponUsageAll extends CouponUsage {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<IssueMethod> issueMethods() {
        return Collections.unmodifiableSet(EnumSet.of(IssueMethod.BULK, IssueMethod.EVENT));
    }
}
