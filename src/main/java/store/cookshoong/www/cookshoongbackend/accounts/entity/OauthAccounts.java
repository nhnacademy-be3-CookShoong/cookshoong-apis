package store.cookshoong.www.cookshoongbackend.accounts.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "oauth_accounts")
public class OauthAccounts implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "oauth_type_id", nullable = false)
    private Integer oauthTypeId;

    @Column(name = "account_code", nullable = false)
    private String accountCode;

}
