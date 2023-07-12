package store.cookshoong.www.cookshoongbackend.common.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SecureKeyManager 서비스에 대한 접속정보를 환경변수로부터 가져오는 클래스.
 *
 * @author koesnam
 * @since 2023.07.10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "cookshoong.skm")
public class SecureKeyManagerProperties {
    private String password;
    private String appkey;
}
