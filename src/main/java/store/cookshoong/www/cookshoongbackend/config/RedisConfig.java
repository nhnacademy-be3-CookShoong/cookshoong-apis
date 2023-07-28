package store.cookshoong.www.cookshoongbackend.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import store.cookshoong.www.cookshoongbackend.common.property.RedisProperties;
import store.cookshoong.www.cookshoongbackend.common.service.SKMService;

/**
 * Redis 를 사용하기 위한 Config 클래스.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Configuration
public class RedisConfig {

    @Bean
    @Profile("!default")
    public RedisProperties redisProperties(@Value("${cookshoong.skm.keyid.redis}") String redisKeyId,
                                           SKMService skmService) throws JsonProcessingException {
        return skmService.fetchSecrets(redisKeyId, RedisProperties.class);
    }

    /**
     * 레디스에 연결하기 위한 configuration.
     *
     * @param redisProperties the redis properties
     * @param database        the database
     * @return                the redis connection factory
     */
    @Bean
    @Profile("!default")
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties,
                                                        @Value("${spring.redis.database}") Integer database) {
        return createRedisConnectionFactory(redisProperties, database);
    }

    @Bean
    @Profile("!default")
    public RedisConnectionFactory couponRedisConnectionFactory(RedisProperties redisProperties,
                                                         @Value("${coupon.redis.database}") Integer database) {
        return createRedisConnectionFactory(redisProperties, database);
    }

    private RedisConnectionFactory createRedisConnectionFactory(RedisProperties redisProperties, Integer database) {

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setPassword(redisProperties.getPassword());
        configuration.setDatabase(database);

        return new LettuceConnectionFactory(configuration);
    }

    /**
     * RedisTemplate 에 대해 모든 타입을 사용할 수 있도록 설정.
     *
     * @param redisConnectionFactory        the redisConnectionFactory
     * @return                              redisTemplate 를 반환
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RedisTemplate<String, Object> couponRedisTemplate(RedisConnectionFactory couponRedisConnectionFactory) {
        return createRedisTemplate(couponRedisConnectionFactory);
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
