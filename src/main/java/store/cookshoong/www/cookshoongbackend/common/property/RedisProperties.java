package store.cookshoong.www.cookshoongbackend.common.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Redis 기본 설정들을 담고 있는 Properties 클래스.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisProperties {
    private Integer port;
    private String host;
    private String password;
}
