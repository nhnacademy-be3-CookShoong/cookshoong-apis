package store.cookshoong.www.cookshoongbackend.coupon.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쿠폰 발행 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "issue_coupons")
public class IssueCoupon {

    @Id
    @Column(name = "issue_coupon_code", nullable = false)
    private String code;

    // TODO: 회원 entity 추가 후 @ManyToOne 작성할 것.
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "issue_at", nullable = false)
    private Date issueAt;

    @Column(name = "expiration_at")
    private Date expirationAt;

}
