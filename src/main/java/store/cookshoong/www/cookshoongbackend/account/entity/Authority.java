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
 * 회원권한 엔티티.
 *
 * @author koesnam
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "authorities")
public class Authority {
    @Id
    @Column(name = "authority_code", nullable = false, length = 10)
    private String authorityCode;

    @Column(nullable = false, length = 10)
    private String description;

    public Authority(String authorityCode, String description) {
        this.authorityCode = authorityCode;
        this.description = description;
    }

    /**
     * 권한 코드를 상수로 관리.
     */
    public enum Code {
        CUSTOMER, BUSINESS, ADMIN;

        public static boolean matches(String authorityCode) {
            return Arrays.stream(Authority.Code.values())
                .anyMatch(a -> a.name().equals(authorityCode));
        }
    }
}
