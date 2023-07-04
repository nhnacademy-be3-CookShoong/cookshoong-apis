package store.cookshoong.www.cookshoongbackend.account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원상태 엔티티.
 *
 * @author koesnam
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "accounts_status")
public class AccountsStatus {
    @Id
    @Column(name = "status_code", nullable = false, length = 10)
    private String statusCode;

    @Column(nullable = false, length = 10)
    private String description;

    public AccountsStatus(String statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }
}
