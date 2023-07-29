package store.cookshoong.www.cookshoongbackend.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.cookshoong.www.cookshoongbackend.common.property.RedisProperties;

/**
 * 분산 락을 위한 redisson 설정.
 *
 * @author eora21 (김주호)
 * @since 2023.07.29
 */
@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    private static final String REDISSON_HOST_PREFIX = "redis://";
    private final RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String host = redisProperties.getHost();
        Integer port = redisProperties.getPort();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
        return Redisson.create(config);
    }
}
