package store.cookshoong.www.cookshoongbackend.cart.redis.exception;

import store.cookshoong.www.cookshoongbackend.common.exception.NotFoundException;

/**
 * Redis 장바구니에 key 가 존재하지 않을 경우 발생하는 Exception.
 *
 * @author jeongjewan
 * @since 2023.07.21
 */
public class NotFoundCartRedisKey extends NotFoundException {

    private static final String MESSAGE = "장바구니 redis Key ";

    public NotFoundCartRedisKey() {
        super(MESSAGE);
    }
}
