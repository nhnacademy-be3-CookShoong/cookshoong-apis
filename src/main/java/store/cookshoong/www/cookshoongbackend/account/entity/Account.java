package store.cookshoong.www.cookshoongbackend.account.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 엔티티.
 *
 * @author koesnam
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rank_code", nullable = false)
    private Rank rank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_code", nullable = false)
    private AccountsStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authority_code", nullable = false)
    private Authority authority;

    @Column(nullable = false, length = 30)
    private String loginId;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "last_login_at", nullable = false)
    private LocalDateTime lastLoginAt;

    /**
     * 회원 생성자.
     *
     * @param loginId     로그인 아이디
     * @param password    비밀번호
     * @param name        이름
     * @param nickname    별명
     * @param email       이메일
     * @param birthday    생년월일
     * @param phoneNumber 전화번호
     */
    public Account(String loginId, String password, String name,
                   String nickname, String email, LocalDate birthday,
                   String phoneNumber) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateRank(Rank rank) {
        this.rank = rank;
    }

    public void updateStatus(AccountsStatus status) {
        this.status = status;
    }

    public void updateAuthority(Authority authority) {
        this.authority = authority;
    }
}
