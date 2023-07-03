package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "accounts")
public class Accounts implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "rank_code", nullable = false)
    private String rankCode;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "authority_code", nullable = false)
    private String authorityCode;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "birthday", nullable = false)
    private Date birthday;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "last_login_at", nullable = false)
    private Date lastLoginAt;

}
