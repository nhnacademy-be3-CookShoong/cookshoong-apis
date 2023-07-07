package store.cookshoong.www.cookshoongbackend.coupon.entity;

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

/**
 * 쿠폰 발행 entity.
 *
 * @author eora21
 * @since 2023.07.04
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue_coupons")
public class IssueCoupon {

    @Id
    @Column(name = "issue_coupon_code", nullable = false)
    private UUID code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id")
    private CouponPolicy couponPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "issue_at", nullable = false)
    private LocalDateTime issueAt;

    @Column(name = "expiration_at")
    private LocalDateTime expirationAt;

    /**
     * 쿠폰 발행 생성자.
     * 쿠폰 식별번호 랜덤 생성, 쿠폰 정책과 발행일 지정.
     *
     * @param couponPolicy 쿠폰 정책
     * @param issueAt      쿠폰 발행일
     */
    public IssueCoupon(CouponPolicy couponPolicy, LocalDateTime issueAt) {
        this.code = UUID.randomUUID();
        this.couponPolicy = couponPolicy;
        this.issueAt = issueAt;
    }

    /**
     * 발행된 쿠폰을 사용자에게 제공.
     *
     * @param account      쿠폰을 제공받을 사용자
     * @param expirationAt 쿠폰 만료일자
     */
    public void provideToUser(Account account, LocalDateTime expirationAt) {
        this.account = account;
        this.expirationAt = expirationAt;
    }
}
