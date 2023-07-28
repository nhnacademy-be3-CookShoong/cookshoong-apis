package store.cookshoong.www.cookshoongbackend.file.model.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 오브젝트 스토리지에 접근하기 위한 인증 정보를 담고 있는 dto.<br>
 * {
 * "auth": {
 * "tenantId": "{Tenant ID}",
 * "passwordCredentials": {
 * "username": "{NHN Cloud ID}",
 * "password": "{API Password}"
 * }
 * }
 * }
 *
 * @author seungyeon (유승연)
 * @since 2023.07.25
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRequest {

    private Auth auth;

    /**
     * Auth 안에 tenantId와 passwordCredentials 정보를 담고 있음.
     */
    @Getter
    @AllArgsConstructor
    public static class Auth {
        String tenantId;
        PasswordCredentials passwordCredentials;
    }

    /**
     * 오브젝트 스토리지 접근하기 위한 id, password 정보.
     */
    @Getter
    @AllArgsConstructor
    public static class PasswordCredentials {
        String username;
        String password;
    }

    /**
     * 오브젝트 스토리지 접근을 위한 토큰 요청 정보 설정.
     *
     * @param tenantId the tenant id
     * @param username the username
     * @param password the password
     */
    public TokenRequest(String tenantId, String username, String password) {
        this.auth = new Auth(tenantId, new PasswordCredentials(username, password));
    }
}
