package store.cookshoong.www.cookshoongbackend.point.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;

/**
 * 포인트 사유 회원가입 entity.
 *
 * @author eora21 (김주호)
 * @since 2023.08.07
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("SIGNUP")
@Table(name = "point_reason_signup")
public class PointReasonSignup extends PointReason {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    Account account;

    public PointReasonSignup(Account account) {
        super("회원가입으로 인한 포인트 적립");
        this.account = account;
    }
}
