package store.cookshoong.www.cookshoongbackend.account.entity;

import java.util.Arrays;
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
@Table(name = "account_status")
public class AccountStatus {
    @Id
    @Column(name = "status_code", nullable = false, length = 10)
    private String statusCode;

    @Column(nullable = false, length = 10)
    private String description;

    public AccountStatus(String statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    /**
     * 회원 상태코드들을 상수로 관리하기 위한 Enum.
     */
    public enum Code {
        ACTIVE, DORMANCY, WITHDRAWAL;

        public static boolean matches(String statusCode) {
            return Arrays.stream(AccountStatus.Code.values())
                .anyMatch(a -> a.name().equals(statusCode));
        }
    }
}
