package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Getter;
import store.cookshoong.www.cookshoongbackend.coupon.exception.NotAllowedIssueMethodException;
import store.cookshoong.www.cookshoongbackend.coupon.util.IssueMethod;

/**
 * 쿠폰 사용처 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@Table(name = "coupon_usage")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "sub_type")
public abstract class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_usage_id", nullable = false)
    private Long id;


    /**
     * 가능한 issue method 정의.
     *
     * @return the set
     */
    protected abstract Set<IssueMethod> issueMethods();

    /**
     * issue method 범위 내에서 요청했는지 확인하는 메서드.
     *
     * @param issueMethod the issue method
     */
    public void validIssueMethod(IssueMethod issueMethod) {
        if (!issueMethods().contains(issueMethod)) {
            throw new NotAllowedIssueMethodException();
        }
    }
}
