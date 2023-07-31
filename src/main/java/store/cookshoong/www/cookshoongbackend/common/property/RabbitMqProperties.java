package store.cookshoong.www.cookshoongbackend.common.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * skm 통해 rabbitMq 정보를 가져오는 property.
 *
 * @author eora21 (김주호)
 * @since 2023.07.28
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RabbitMqProperties {
    private String hostname;
    private String username;
    private String password;
    private String virtualHost;
    private Integer port;
}
