package store.cookshoong.www.cookshoongbackend.cart.redis.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.InvalidStoreException;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundCartRedisKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundMenuRedisHashKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.repository.CartRedisRepository;

/**
 * Redis 를 이용한 장바구니 서비스.
 *
 * @author jeongjewan_정제완
 * @since 2023.07.21
 */
@Service
@RequiredArgsConstructor
public class CartRedisService {

    private final CartRedisRepository cartRedisRepository;

    /**
     * 장바구니에 담는 메뉴를 Redis 에 저장하는 메서드.
     * 하나의 매장에서만 담을 수 있도록 제한.
     *
     * @param redisKey      redis key
     * @param hashKey        redis hashKey
     * @param cart    장바구니에 담기는 메뉴 Dto
     */
    public void createCartMenu(String redisKey, String hashKey, CartRedisDto cart) {

        List<Object> cartRedis = cartRedisRepository.findByCartAll(redisKey);
        Long storeId = null;

        if (!cartRedis.isEmpty()) {
            CartRedisDto cartValue = (CartRedisDto) cartRedis.get(0);
            storeId = cartValue.getStoreId();

            if (!storeId.equals(cart.getStoreId())) {
                throw new InvalidStoreException();
            }
        }

        if (cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {
            cart.incrementCount();
        }

        cart.createTimeMillis();
        cart.setHashKey(cart.generateUniqueHashKey());

        cartRedisRepository.cartRedisSave(redisKey, hashKey, cart);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 대해 수정되는 메서드.
     * 옵션에 대한 수정을 할 때 hashKey 가 달라지면 추가를 해버리는 문제가 발생.
     * 이때 변경하기전에 대한 메뉴 hashKey 를 가져오기때문에 그 key 를 가지고 삭제를 한후
     * 변경된 메뉴에 대해서 새로운 hashKey 를 만들고 그 키를 가지고 Redis 장바구니에 담는다.
     *
     * @param redisKey      redis key
     * @param hashKey        redis hashKey
     * @param cart    장바구니에서 수정되는 Dto
     */
    public void modifyCartMenuRedis(String redisKey, String hashKey, CartRedisDto cart) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        } else if (cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {
            cartRedisRepository.deleteCartMenu(redisKey, hashKey);
        }

        cart.setRefreshHashKey(cart.generateUniqueHashKey());

        cartRedisRepository.cartMenuRedisModify(redisKey, cart.getHashKey(), cart);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 수량을 늘리는 메서드.
     * Front 에서 플러스 버튼을 누르면
     * Gateway 를 타고 Back Api 로 와서 수량을 늘려준다.
     *
     * @param redisKey      redis key
     * @param hashKey        redis hashKey
     */
    public void modifyCartMenuIncrementCount(String redisKey, String hashKey) {

        CartRedisDto cart = null;
        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        } else if (cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {

            cart = (CartRedisDto) cartRedisRepository.findByCartMenu(redisKey, hashKey);
            cart.incrementCount();
        }

        cartRedisRepository.cartMenuRedisModify(redisKey, hashKey, cart);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 수량을 줄이는 메서드.
     * Front 에서 플러스 버튼을 누르면
     * Gateway 를 타고 Back Api 로 와서 수량을 줄여준다.
     *
     * @param redisKey      redis key
     * @param hashKey        redis hashKey
     */
    public void modifyCartMenuDecrementCount(String redisKey, String hashKey) {
        CartRedisDto cart = null;
        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        } else if (cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {

            cart = (CartRedisDto) cartRedisRepository.findByCartMenu(redisKey, hashKey);
            cart.decrementCount();
        }

        cartRedisRepository.cartMenuRedisModify(redisKey, hashKey, cart);
    }

    /**
     * Redis 에 해당 key 에 저장되어 있는 모든 메뉴들을 List 형태로 전달하는 메서드.
     *
     * @param redisKey      redis key
     * @return              해당 key 모든 메뉴들을 반환
     */
    public List<CartRedisDto> selectCartMenuAll(String redisKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        }

        List<Object> cartRedis = cartRedisRepository.findByCartAll(redisKey);
        List<CartRedisDto> carts = new ArrayList<>();

        for (Object cart : cartRedis) {
            carts.add((CartRedisDto) cart);
        }

        // 장바구니에 등록된 순서대로 정렬
        Comparator<CartRedisDto> sortCarts = Comparator.comparing(CartRedisDto::getCreateTimeMillis);
        carts = carts.stream().sorted(sortCarts).collect(Collectors.toList());

        return carts;
    }

    /**
     * Redis 장바구니 담겨져 있는 특정 메뉴를 가져오는 메서드.
     *
     * @param redisKey      redis key
     * @param hashKey        메뉴 아이디
     * @return              key 에 해당되는 메뉴 아이디를 통해 메뉴를 반환.
     */
    public CartRedisDto selectCartMenu(String redisKey, String hashKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        } else if (!cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {
            throw new NotFoundMenuRedisHashKey();
        }

        return (CartRedisDto) cartRedisRepository.findByCartMenu(redisKey, hashKey);
    }

    /**
     * Redis 장바구니 담겨져 있는 메뉴들에 개수를 가져오는 메서드.
     *
     * @param redisKey      redis key
     * @return              장바구니 개수를 반환
     */
    public Long selectCartCount(String redisKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        }

        return cartRedisRepository.cartRedisSize(redisKey);
    }

    /**
     * Redis 장바구니에서 해당 메뉴를 삭제하는 메서드.
     *
     * @param redisKey      redis key
     * @param hashKey        메뉴 아이디
     */
    public void removeCartMenu(String redisKey, String hashKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        } else if (!cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {
            throw new NotFoundMenuRedisHashKey();
        }

        cartRedisRepository.deleteCartMenu(redisKey, hashKey);
    }

    /**
     * Redis 장바구니에 모든 메뉴를 삭제.
     * key 를 삭제하는 것이기 때문에 다시 Cookie 로 생성해줘야 한다.
     *
     * @param redisKey      redis key
     */
    public void removeCartMenuAll(String redisKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        }

        cartRedisRepository.deleteCartAll(redisKey);
    }
}
