package store.cookshoong.www.cookshoongbackend.cart.redis.repository;

import static store.cookshoong.www.cookshoongbackend.cart.utils.CartConstant.PHANTOM;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 에 대한 CRUD 기능 Repository.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Component
@RequiredArgsConstructor
public class CartRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 메뉴를 장바구니에 담으면 Redis 에 저장되는 메서드.  <br>
     * 장바구니 생성 시 두 개 key 를 생성해준다 <br>
     * redis 에서 장바구니가 사라질 때 Listener 로 잡을 Phantom Key 와 기존 장바구니 키를 생성 <br>
     *
     * @param redisKey       redis key
     * @param hashKey        redis hashKey
     * @param cartRequest   저장될 객체
     */
    public void cartRedisSave(String redisKey, String hashKey, Object cartRequest) {

        redisTemplate.opsForHash().put(redisKey, hashKey, cartRequest);
        redisTemplate.opsForHash().put(redisKey + PHANTOM, hashKey, "");
        redisTemplate.expire(redisKey + PHANTOM, 10, TimeUnit.SECONDS);
        redisTemplate.expire(redisKey, 15, TimeUnit.SECONDS);
    }

    /**
     * Lock 을 구분할 수 있는 redis Key 를 생성. <br>
     * 동시성 문제로 인해 먼저 들어온 장바구니에 대해서 처리를 진행한뒤 Lock 에 대한 key 생성해서 <br>
     * 다음번에 들어오는 장바구니에 대해서는 이 Lock 존재시 다음 작업 수행하지 못하도록 진행
     *
     * @param lockKey       lock redis key
     * @param hashKey       lock redis hashKey
     */
    public void createLockRedis(String lockKey, String hashKey) {

        redisTemplate.opsForHash().put(lockKey, hashKey, "lock");
        redisTemplate.expire(lockKey, 10, TimeUnit.SECONDS);
    }

    /**
     * key 에 해당되는 특정 hashKey 에 값을 새로운 객체로 덮어씌움으로 Redis 객체를 변경해주는 메서드.
     *
     * @param redisKey    redis key
     * @param hashKey      redis hashKey
     * @param cartRequest 업데이트될 객체
     */
    public void cartMenuRedisModify(String redisKey, String hashKey, Object cartRequest) {


        redisTemplate.opsForHash().put(redisKey, hashKey, cartRequest);
    }

    /**
     * key 에 저장된 모든 hash 를 Redis 에서 모두 가져오는 메서드.
     *
     * @param redisKey redis key
     * @return redis key 들어있는 정보를 모두 가져와서 반환
     */
    public List<Object> findByCartAll(String redisKey) {


        return redisTemplate.opsForHash().values(redisKey);
    }

    /**
     * key 에 저장된 특정한 hash 에 대한 객체를 Redis 에서 가져오는 메서드.
     *
     * @param redisKey      redis key
     * @param hashKey       redis hashKey
     * @return              redis key 에 특정 hashKey 에 객체를 반환
     */
    public Object findByCartMenu(String redisKey, String hashKey) {


        return redisTemplate.opsForHash().get(redisKey, hashKey);
    }

    /**
     * Redis 장바구니에 담겨 있는 메뉴에 수는 가져오는 메서드.
     *
     * @param redisKey      redis key
     * @return              redis key 에 들어있는 수를 반환
     */
    public Long cartRedisSize(String redisKey) {


        return redisTemplate.opsForHash().size(redisKey);
    }

    /**
     * key 에 해당되는 특정 hashKey 를 Redis 에서 삭제하는 메서드.
     *
     * @param redisKey       redis key
     * @param hashKey        redis hashKey
     */
    public void deleteCartMenu(String redisKey, String hashKey) {


        redisTemplate.opsForHash().delete(redisKey + PHANTOM, hashKey);
        redisTemplate.opsForHash().delete(redisKey, hashKey);
    }

    /**
     * key 에 해당되는 장바구니 내역을 Redis 에서 모두 삭제하는 메서드.
     *
     * @param redisKey      redis Key
     */
    public void deleteCartAll(String redisKey) {


        redisTemplate.delete(redisKey + PHANTOM);
        redisTemplate.delete(redisKey);
    }

    /**
     * Redis 에서 key 에 해당되는 hashKey 가 존재하는지 확인하는 메서드.
     *
     * @param redisKey      redis key
     * @param hashKey       redis hashKey
     * @return              해당 hashKey 존재여부를 반환
     */
    public boolean existMenuInCartRedis(String redisKey, String hashKey) {


        return redisTemplate.opsForHash().hasKey(redisKey, hashKey);
    }

    /**
     * Redis 에서 key 가 존재하는지 확인하는 메서드.
     *
     * @param redisKey Redis key
     * @return 해당 key 존재 여부를 반환
     */
    public boolean existKeyInCartRedis(String redisKey) {


        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    /**
     * 장바구니 key 들어 있는 모든 hashKey.
     *
     * @param redisKey      redis key
     * @return              hashKey 반환
     */
    public Set<Object> cartKeyInHashKey(String redisKey) {


        return redisTemplate.opsForHash().keys(redisKey);
    }
}
