package store.cookshoong.www.cookshoongbackend.cart.redis.exception;

import org.springframework.validation.BindingResult;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;

/**
 * Redis 장바구니에서 생성에 대한 Valid Exception.
 *
 * @author jeongjewan
 * @since 2023.07.23
 */
public class CreateCartRedisValidationException extends ValidationFailureException {

    public CreateCartRedisValidationException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

