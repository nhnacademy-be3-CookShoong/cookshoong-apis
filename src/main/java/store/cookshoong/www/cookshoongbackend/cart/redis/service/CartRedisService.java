package store.cookshoong.www.cookshoongbackend.cart.redis.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.cart.db.model.response.CartResponseDto;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartRepository;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.InvalidStoreException;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundCartRedisKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundMenuRedisHashKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.repository.CartRedisRepository;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;

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
    private final CartRepository cartRepository;
    private static final String NO_MENU = "NO_KEY";
    public static final String CART = "cartKey=";
    private final ObjectStorageService objectStorageService;

    /**
     * 장바구니에 담는 메뉴를 Redis 에 저장하는 메서드. <br>
     * 빈 장바구니 hashKey 가 존재하면 삭제하고 Redis 장바구니 생성 <br>
     * 하나의 매장에서만 담을 수 있도록 제한 <br>
     * 같은 메뉴가 들어오면 redis 에 저장되어 있는 값을 불러와서 수량과 합계를 변경해서 저장
     *
     * @param redisKey      redis key
     * @param hashKey        redis hashKey
     * @param cart    장바구니에 담기는 메뉴 Dto
     */
    public void createCartMenu(String redisKey, String hashKey, CartRedisDto cart) {

        List<Object> cartRedis = cartRedisRepository.findByCartAll(redisKey);
        Long storeId = null;

        if (cartRedisRepository.existMenuInCartRedis(redisKey, NO_MENU)) {
            cartRedisRepository.deleteCartMenu(redisKey, NO_MENU);
        } else if (cartRedis != null && !cartRedis.isEmpty()) {
            CartRedisDto cartValue = (CartRedisDto) cartRedis.get(0);
            storeId = cartValue.getStoreId();

            if (!storeId.equals(cart.getStoreId())) {
                throw new InvalidStoreException();
            }
        }

        if (cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)) {
            CartRedisDto redisCart = (CartRedisDto) cartRedisRepository.findByCartMenu(redisKey, hashKey);
            redisCart.setCount(cart.getCount() + redisCart.getCount());
            redisCart.setTotalMenuPrice(redisCart.generateTotalMenuPrice());
            cartRedisRepository.cartRedisSave(redisKey, hashKey, redisCart);
            return;
        }

        cart.createTimeMillis();
        cart.setHashKey(cart.generateUniqueHashKey());
        cart.setMenuOptName(cart.generateMenuOptionName());
        cart.setTotalMenuPrice(cart.generateTotalMenuPrice());

        cartRedisRepository.cartRedisSave(redisKey, hashKey, cart);
    }

    /**
     * 빈 장바구니를 생성하는 메소드.    <br>
     * DB 장바구니에 접근을 최소화하기 위해 빈 장바구니를 생성.
     *
     * @param cartKey       redis key
     * @param noKey         redis hashKey
     */
    public void createCartEmpty(String cartKey, String noKey) {

        cartRedisRepository.cartRedisSave(cartKey, noKey, null);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 대해 수정되는 메서드.    <br>
     * 옵션에 대한 수정을 할 때 hashKey 가 달라지면 추가를 해버리는 문제가 발생.   <br>
     * 이때 변경하기전에 대한 메뉴 hashKey 를 가져오기때문에 그 key 를 가지고 삭제를 한후 <br>
     * 변경된 메뉴에 대해서 새로운 hashKey 를 만들고 그 키를 가지고 Redis 장바구니에 담는다.  <br>
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

        cart.setHashKey(cart.generateUniqueHashKey());
        cart.setMenuOptName(cart.generateMenuOptionName());
        cart.setTotalMenuPrice(cart.generateTotalMenuPrice());

        if (hashKey.equals(cart.getHashKey())) {
            cart.incrementCount();
        }

        cartRedisRepository.cartMenuRedisModify(redisKey, cart.getHashKey(), cart);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 수량을 늘리는 메서드.    <br>
     * Front 에서 플러스 버튼을 누르면 <br>
     * Gateway 를 타고 Back Api 로 와서 수량을 늘려준다. <br>
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

        assert cart != null;
        cart.setTotalMenuPrice(cart.generateTotalMenuPrice());

        cartRedisRepository.cartMenuRedisModify(redisKey, hashKey, cart);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 수량을 줄이는 메서드.    <br>
     * Front 에서 플러스 버튼을 누르면 <br>
     * Gateway 를 타고 Back Api 로 와서 수량을 줄여준다. <br>
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

        assert cart != null;
        cart.setTotalMenuPrice(cart.generateTotalMenuPrice());

        cartRedisRepository.cartMenuRedisModify(redisKey, hashKey, cart);
    }

    /**
     * Redis 에 해당 key 에 저장되어 있는 모든 메뉴들을 List 형태로 전달하는 메서드. <br>
     * 장바구니를 클릭하면 hashKey 가 "NOMENU" 인지 먼지 확인한다 -> 있으면 빈 장바구니를 반환 <br>
     * 없으면 Redis Key 가 존재한지 확인하다 -> 없으면 무조건 DB 에 있다  <br>
     * -> 왜냐하면 NOMENU 상태가 Redis, DB 둘다 장바구니가 존재하지 않을 때 들어가는 key 이기 때문이다. <br>
     * 그렇게 없으면 DB 에 있는 장바구니 정보를 가지고 와서 Redis 장바구니로 저장.
     *
     * @param redisKey      redis key
     * @return              해당 key 모든 메뉴들을 반환
     */
    public List<CartRedisDto> selectCartMenuAll(String redisKey) {


        List<CartRedisDto> carts = new ArrayList<>();
        String userId = redisKey.replaceAll(CART, "");


        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            if (cartRepository.hasCartByAccountId(Long.valueOf(userId))) {
                // DB 장바구니 데이터를 가지고 와서 Redis 장바구니에 저장.
                createAllCartFromDbToRedis(redisKey, cartRepository.lookupCartDbList(Long.valueOf(userId)));
            } else {
                cartRedisRepository.cartRedisSave(redisKey, NO_MENU, null);
                CartRedisDto cartRedisDto = (CartRedisDto) cartRedisRepository.findByCartMenu(redisKey, NO_MENU);
                carts.add(cartRedisDto);

                return carts;
            }
        }


        List<Object> cartRedis = cartRedisRepository.findByCartAll(redisKey);

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

        if (cartRedisRepository.cartRedisSize(redisKey) == 1) {
            cartRedisRepository.deleteCartMenu(redisKey, hashKey);
            createCartEmpty(redisKey, NO_MENU);
            return;
        }

        cartRedisRepository.deleteCartMenu(redisKey, hashKey);
    }

    /**
     * Redis 장바구니에 모든 메뉴를 삭제.   <br>
     * key 를 삭제하는 것이기 때문에 다시 Cookie 로 생성해줘야 한다. <br>
     *
     * @param redisKey      redis key
     */
    public void removeCartMenuAll(String redisKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        }

        cartRedisRepository.deleteCartAll(redisKey);
        cartRedisRepository.cartRedisSave(redisKey, NO_MENU, null);
    }

    /**
     * Redis 장바구니에 redisKey 가 존재하는지 확인하는 메서드.
     *
     * @param redisKey      redis Key
     * @return              redis Key 존재여부를 반환
     */
    public boolean hasKeyInCartRedis(String redisKey) {

        return cartRedisRepository.existKeyInCartRedis(redisKey);
    }

    /**
     * Redis 장바구니에 redisKey 에 hashKey 존재하는지 확인하는 메서드.
     *
     * @param redisKey      redis Key
     * @param menuKey       redis hashKey
     * @return              redis Key 존재여부를 반환
     */
    public boolean hasMenuInCartRedis(String redisKey, String menuKey) {

        return cartRedisRepository.existMenuInCartRedis(redisKey, menuKey);
    }

    /**
     *  Db 장바구니 정보를 Redis 로 저장하는 메서드.
     *
     * @param accountId     회원 아이디
     */
    public void createDbCartUploadRedis(String redisKey, Long accountId) {

        createAllCartFromDbToRedis(redisKey, cartRepository.lookupCartDbList(accountId));
    }

    /**
     * DB 장바구니에서 가져온 정보를 Redis 장바구니에 저장하는 메서드.
     *
     * @param cartResponseDtos      DB 장바구니 정보
     */
    private void createAllCartFromDbToRedis(String redisKey, List<CartResponseDto> cartResponseDtos) {
        for (CartResponseDto cartResponseDto : cartResponseDtos) {

            CartMenuResponseDto cartMenuResponseDto = cartResponseDto.getCartMenuResponseDto();
            CartMenuDto cartMenuRedisDto =
                new CartMenuDto(cartMenuResponseDto.getMenuId(), cartMenuResponseDto.getName(),
                    objectStorageService.getFullPath(FileDomain.MENU_IMAGE.getVariable(), cartMenuResponseDto.getSavedName()), cartMenuResponseDto.getPrice());


            List<CartOptionResponseDto> cartOptionResponseDtos = cartResponseDto.getCartOptionResponseDto();
            List<CartOptionDto> cartOptionRedisDtos = new ArrayList<>();

            for (CartOptionResponseDto cartOptionResponseDto : cartOptionResponseDtos) {
                CartOptionDto cartOptionRedisDto =
                    new CartOptionDto(cartOptionResponseDto.getOptionId(),
                        cartOptionResponseDto.getName(), cartOptionResponseDto.getPrice());
                cartOptionRedisDtos.add(cartOptionRedisDto);
            }

            CartRedisDto cartRedisDto =
                new CartRedisDto(cartResponseDto.getAccountId(), cartResponseDto.getStoreId(),
                    cartResponseDto.getName(), cartMenuRedisDto, cartOptionRedisDtos,
                    cartMenuResponseDto.getCreateTimeMillis(), null, cartMenuResponseDto.getCount(), null, null);

            cartRedisDto.setHashKey(cartRedisDto.generateUniqueHashKey());
            cartRedisDto.setMenuOptName(cartRedisDto.generateMenuOptionName());
            cartRedisDto.setTotalMenuPrice(cartRedisDto.generateTotalMenuPrice());

            cartRedisRepository.cartRedisSave(redisKey, cartRedisDto.getHashKey(), cartRedisDto);
        }
    }

    /**
     * 장바구니에 담긴 메뉴들의 총 가격을 계산하는 메서드.
     *
     * @param cartItems the cart items
     * @return the total price
     */
    public int getTotalPrice(List<CartRedisDto> cartItems) {
        return cartItems.stream()
            .mapToInt(cartRedisDto -> Integer.parseInt(cartRedisDto.getTotalMenuPrice()))
            .reduce(Integer::sum)
            .orElseThrow(NumberFormatException::new);
    }
}
