package store.cookshoong.www.cookshoongbackend.coupon.repository;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

/**
 * 쿠폰 관련 사항들을 Redis db에 처리하는 repository.
 *
 * @author eora21 (김주호)
 * @since 2023.08.01
 */
@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {
    private final RedisTemplate<String, Object> couponRedisTemplate;

    /**
     * redis db에 쿠폰 코드를 Bulk Insert 수행하는 메서드.
     *
     * @param couponCodes    the coupon codes
     * @param couponPolicyId the coupon policy id
     */
    @SuppressWarnings("unchecked")
    public void bulkInsertCouponCode(Set<UUID> couponCodes, String couponPolicyId) {
        RedisSerializer<String> keySerializer = couponRedisTemplate.getStringSerializer();
        RedisSerializer<UUID> valueSerializer = (RedisSerializer<UUID>) couponRedisTemplate.getValueSerializer();

        couponRedisTemplate.executePipelined((RedisCallback<Object>) redisConnection -> {
            couponCodes.forEach(couponCode ->
                redisConnection.sAdd(
                    Objects.requireNonNull(keySerializer.serialize(couponPolicyId)),
                    valueSerializer.serialize(couponCode)));

            return null;
        });
    }

    /**
     * redis key 존재 여부.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean hasKey(String key) {
        Boolean hasKey = couponRedisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    /**
     * redis set 획득.
     *
     * @param key the key
     * @return the redis set
     */
    public BoundSetOperations<String, Object> getRedisSet(String key) {
        return couponRedisTemplate.boundSetOps(key);
    }
}
