package store.cookshoong.www.cookshoongbackend.cart.redis.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.cart.db.service.CartService;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;

/**
 * Phantom Key 를 잡기위한 Listener.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
@Slf4j
@Component
public class CartKeyExpiredEventListener extends KeyExpirationEventMessageListener {

    private static final String PHANTOM = ":phantom";
    private final CartService cartService;
    private final CartRedisService cartRedisService;

    /**
     * CartKeyExpiredEventListener 생성자.
     *
     * @param listenerContainer     만료된 key 에 대한 MessageListener
     * @param cartService           DB 장바구니에 대한 Service
     */
    public CartKeyExpiredEventListener(RedisMessageListenerContainer listenerContainer,
                                       CartService cartService, CartRedisService cartRedisService) {

        super(listenerContainer);
        this.cartService = cartService;
        this.cartRedisService = cartRedisService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String expiredRedisKey = message.toString();

         log.error("EXPIRED KEY: {}", expiredRedisKey);

        if (!expiredRedisKey.endsWith(PHANTOM)) {
            return;
        }

        String redisKey = expiredRedisKey.replaceAll(PHANTOM, "");
        log.error("REDIS KEY: {}", redisKey);

        List<CartRedisDto> cartRedisList = cartRedisService.selectCartMenuAll(redisKey);

        cartService.createCartDb(redisKey, cartRedisList);
    }
}
