package store.cookshoong.www.cookshoongbackend.cart.redis.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * Redis 장바구니에 해당 key 메뉴에 대한 hashKey 가 존재하지 않을 때 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.21
 */
public class NotFoundMenuRedisHashKey extends NotFoundException {

    private static final String MESSAGE = "메뉴 hashKey ";

    public NotFoundMenuRedisHashKey() {
        super(MESSAGE);
    }
}
