package store.cookshoong.www.cookshoongbackend.coupon.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "issue_coupons")
public class IssueCoupons implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "issue_coupon_code", nullable = false)
    private String issueCouponCode;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "issue_at", nullable = false)
    private Date issueAt;

    @Column(name = "expiration_at")
    private Date expirationAt;

}
