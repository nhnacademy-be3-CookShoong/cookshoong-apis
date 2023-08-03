package store.cookshoong.www.cookshoongbackend.account.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소셜 로그인 엔티티.
 *
 * @author koesnam
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "oauth_accounts")
public class OauthAccount {

    @EmbeddedId
    private Pk pk;

    @MapsId("id")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @MapsId("oauthTypeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_type_id", nullable = false)
    private OauthType oauthType;

    @Column(name = "account_code", nullable = false)
    private String accountCode;

    /**
     * OAuthAccount 복합키.
     */
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Pk implements Serializable {
        private Long id;
        private Integer oauthTypeId;
    }

    /**
     * Instantiates a new Oauth account.
     *
     * @param account     the account
     * @param oauthType   the oauth type
     * @param accountCode the account code
     */
    public OauthAccount(Account account, OauthType oauthType, String accountCode) {
        this.pk = new Pk(account.getId(), oauthType.getId());
        this.account = account;
        this.oauthType = oauthType;
        this.accountCode = accountCode;
    }
}
