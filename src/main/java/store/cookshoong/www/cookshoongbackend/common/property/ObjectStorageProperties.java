package store.cookshoong.www.cookshoongbackend.common.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Secure Key Manager 로 부터 Object Storage 정보를 가져오기 위한 dto.
 * ex_
 * {
 *      "tenantId": "...",
 *      "username" : "...",
 *      "password" : "...",
 *      "containerName" : "...",
 *      "storageUrl" : "..."
 * }
 * @author seungyeon (유승연)
 * @since 2023.07.26
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObjectStorageProperties {
    private String tenantId;
    private String username;
    private String password;
    private String containerName;
    private String storageUrl;
    private String authUrl;
}
