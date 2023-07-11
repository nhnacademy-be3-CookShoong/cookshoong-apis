package store.cookshoong.www.cookshoongbackend.common.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Secure Key Manager 로 부터 DB 정보를 가져오기 위한 DTO.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DatabaseProperties {
    @JsonProperty("datasource")
    private DatabaseProperty databaseProperty;

    /**
     * ex)
     * "datasource" : {
     *     "driverClassName": "...",
     *     "url" : "...",
     *     "username" : "...",
     *     "password" : "..."
     * }
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DatabaseProperty {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }

    public String getDriverClassName() {
        return getDatabaseProperty().getDriverClassName();
    }

    public String getUrl() {
        return getDatabaseProperty().getUrl();
    }

    public String getUsername() {
        return getDatabaseProperty().getUsername();
    }

    public String getPassword() {
        return getDatabaseProperty().getPassword();
    }
}
