package store.cookshoong.www.cookshoongbackend.common.property;

import java.net.URI;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * SKM API 호출 주소들이 담긴 클래스.
 *
 * @author koesnam (추만석)
 * @since 2023.07.12
 */
public class SecureKeyManagerUri {
    private static final String SCHEME = "https";
    private static final String HOST = "api-keymanager.nhncloudservice.com";
    private static final String APP_NAME = "keymanager";
    private static final String VERSION = "v1.0";

    private SecureKeyManagerUri() {
    }

    /**
     * 입력한 버전의 appkey 와 keyid 를 통해 기밀 데이터를 얻어오는 API 호출을 위한 URL.
     * (v1.0 API 호출)
     *
     * @param appkey Secure Manager Key 에 등록한 그룹이 가지는 appkey
     * @param keyid  the keyid 기밀 데이터의 아이디
     * @return 기밀데이터 호출 URL
     */
    public static URI getSecretUri(String appkey, String keyid) {
        return UriComponentsBuilder.newInstance()
            .scheme(SCHEME)
            .host(HOST)
            .pathSegment(APP_NAME)
            .pathSegment(VERSION)
            .pathSegment("appkey")
            .path("{appkey}")
            .pathSegment("secrets")
            .path("{keyid}")
            .buildAndExpand(appkey, keyid)
            .encode()
            .toUri();
    }
}
