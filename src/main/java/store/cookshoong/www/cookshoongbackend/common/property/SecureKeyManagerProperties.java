package store.cookshoong.www.cookshoongbackend.common.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {설명을 작성해주세요}
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ConfigurationProperties("cookshoong.secure-key-manager")
public class SecureKeyManagerProperties {
    private String appkey;
    private String keyid;
    private String password;
}
