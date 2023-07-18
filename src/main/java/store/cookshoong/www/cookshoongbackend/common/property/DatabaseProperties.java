package store.cookshoong.www.cookshoongbackend.common.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Secure Key Manager 로 부터 DB 정보를 가져오기 위한 DTO.
 * ex_
 * {
 *      "driverClassName": "...",
 *      "url" : "...",
 *      "username" : "...",
 *      "password" : "..."
 * }
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DatabaseProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
