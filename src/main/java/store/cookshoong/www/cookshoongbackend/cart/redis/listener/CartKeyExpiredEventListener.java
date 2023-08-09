package store.cookshoong.www.cookshoongbackend.cart.redis.listener;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import store.cookshoong.www.cookshoongbackend.cart.db.service.CartService;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;

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
    private static final String NO_MENU = "NO_KEY";
    private static final String CART = "cartKey=";
    private final CartService cartService;
    private final CartRedisService cartRedisService;
    private final LockProcessor lockProcessor;

    /**
     * CartKeyExpiredEventListener 생성자.
     *
     * @param listenerContainer     만료된 key 에 대한 MessageListener
     * @param cartService           DB 장바구니에 대한 Service
     */
    public CartKeyExpiredEventListener(RedisMessageListenerContainer listenerContainer,
                                       CartService cartService, CartRedisService cartRedisService,
                                       LockProcessor lockProcessor) {

        super(listenerContainer);
        this.cartService = cartService;
        this.cartRedisService = cartRedisService;
        this.lockProcessor = lockProcessor;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        List<CartRedisDto> cartRedisList = new ArrayList<>();

        String expiredRedisKey = message.toString();

        if (!expiredRedisKey.endsWith(PHANTOM)) {
            return;
        }

        String redisKey = expiredRedisKey.replaceAll(PHANTOM, "");

        // Lock을 사용하여 처리할 작업을 묶어줍니다.
        List<CartRedisDto> finalCartRedisList = cartRedisService.selectCartMenuAll(redisKey);

        lockProcessor.lock(redisKey, ignore -> {
            if (cartRedisService.hasMenuInCartRedis(redisKey, NO_MENU)) {
                cartService.createCartDb(expiredRedisKey, cartRedisList);
            } else {
                if (!finalCartRedisList.isEmpty()) {
                    cartService.createCartDb(expiredRedisKey, finalCartRedisList);
                }
            }
        });
    }
}
