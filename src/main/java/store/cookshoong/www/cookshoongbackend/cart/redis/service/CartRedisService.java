package store.cookshoong.www.cookshoongbackend.cart.redis.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.DuplicationMenuException;
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
            throw new DuplicationMenuException();
        }

        cart.createTimeMillis();
        cart.setRefreshHashKey(cart.generateUniqueHashKey());

        cartRedisRepository.cartRedisSave(redisKey, hashKey, cart);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 대해 수정되는 메서드.
     * 장바구니에서 수정은 옵션 위 주에 수정만 생각한다.
     * 이때 수정은 hashKey 가 변하기 때문에 수정하기 전에 메뉴에 대해서
     * 삭제 후 새로 put 하는 형식으로 변경을 구현한다.
     * 이렇게 되면 새로 등록하는게 되기 때문에 순서가 유지가 안되는데
     * 순서를 유지하려면 삭제하기전에 데이터를 가지고와서 그 데이터에 생성 시간값을 수정한 값에 주입해주면 될거 같다.
     * 이 작업은 아마 프론트에서 주입해주면 될거 같다.
     * 그러면 Back Api 에 들어오는 수정내용에서는 haskKey 만 새롭게 생성해주면 순서가 유지된 채로 장바구니에 보일 것이다.
     *
     * @param redisKey      redis key
     * @param hashKey        redis hashKey
     * @param cart    장바구니에서 수정되는 Dto
     */
    public void modifyCartMenuRedis(String redisKey, String hashKey, CartRedisDto cart) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        } else if (cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {
            throw new DuplicationMenuException();
        }

        cart.setRefreshHashKey(cart.generateUniqueHashKey());

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
