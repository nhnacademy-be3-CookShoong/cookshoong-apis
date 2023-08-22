package store.cookshoong.www.cookshoongbackend.cart.redis.service;

import static store.cookshoong.www.cookshoongbackend.cart.utils.CartConstant.CART;
import static store.cookshoong.www.cookshoongbackend.cart.utils.CartConstant.NO_MENU;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;

/**
 * Redis 를 이용한 장바구니 서비스.
 *
 * @author jeongjewan_정제완
 * @since 2023.07.21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartRedisService {

    private final CartRedisRepository cartRedisRepository;
    private final CartRepository cartRepository;
    public final FileUtilResolver fileUtilResolver;

    /**
     * 장바구니에 담는 메뉴를 Redis 에 저장하는 메서드. <br>
     * 빈 장바구니 hashKey 가 존재하면 삭제하고 Redis 장바구니 생성, <br>
     * 삭제를 해줘야 redisKey 조회시 필요없는 정보가 같이 조회되는 일이 없어진다 <br>
     * 하나의 매장에서만 담을 수 있도록 제한 <br>
     * 같은 메뉴가 들어오면 redis 에 저장되어 있는 값을 불러와서 수량과 합계를 변경해서 저장
     *
     * @param redisKey redis key
     * @param hashKey  redis hashKey
     * @param cart     장바구니에 담기는 메뉴 Dto
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
     * @param cartKey redis key
     * @param noKey   redis hashKey
     */
    public void createCartEmpty(String cartKey, String noKey) {

        cartRedisRepository.cartRedisSave(cartKey, noKey, null);
    }

    /**
     * 장바구니에 담아져 있는 메뉴에 대해 수정되는 메서드.    <br>
     * 먼저 장바구니에 대한 key 존재여부를 확인 후 key 에 해당되는 메뉴 또한 존재하는지 확인 <br>
     * 메뉴가 존재하면 그 메뉴에 대해서 삭제 후 생성, 그 이유는 옵션 변경시 장바구니 put 되는 방식으로 삭제가 없으면 변경이 아닌 추가가 발생<br>
     * 변경된 메뉴에 대해서 필요한 값을 생성 후, 동일한 메뉴를 추가하게 되면 수량을 증가시킨다. <br>
     *
     * @param redisKey redis key
     * @param hashKey  redis hashKey
     * @param cart     장바구니에서 수정되는 Dto
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
     * 장바구니에 담아져 있는 메뉴에 수량을 늘리는 메서드. <br>
     * 장바구니에 대한 key 와 그에 해당되는 메뉴에 대해 존재여부를 먼저 확인 <br>
     * 그 이후 해당 메뉴를 가지고 와서 수량을 늘리고, 메뉴에 대한 금액을 변경해준다 <br>
     *
     * @param redisKey redis key
     * @param hashKey  redis hashKey
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
     * 장바구니에 대한 key 와 그에 해당되는 메뉴에 대해 존재여부를 먼저 확인 <br>
     * 그 이후 해당 메뉴를 가지고 와서 수량을 줄이고, 메뉴에 대한 금액을 변경해준다 <br>
     *
     * @param redisKey redis key
     * @param hashKey  redis hashKey
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
     * 먼저, Redis 에 해당 key 에 대한 데이터가 있는지 확인한다. <br>
     * 만약 없으면 DB 장바구니에 해당 회원에 대한 데이터가 있는지 확인하고 있으면 그 데이터를 불러와서 Redis에 저장시킨다. <br>
     * 만약 Redis, DB 둘 다 존재하지 않을 시에는 빈 장바구니를 생성해주도록 한다. -> 그 이유는 장바구니 조회 시 DB 접근을 최소화하기 위해서이다.
     *
     * @param redisKey redis key
     * @return 해당 key 모든 메뉴들을 반환
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
     * @param redisKey redis key
     * @param hashKey  메뉴 아이디
     * @return key 에 해당되는 메뉴 아이디를 통해 메뉴를 반환.
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
     * @param redisKey redis key
     * @return 장바구니 개수를 반환
     */
    public Long selectCartCount(String redisKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            return 0L;
        }

        return cartRedisRepository.cartRedisSize(redisKey);
    }

    /**
     * Redis 장바구니에서 해당 메뉴를 삭제하는 메서드. <br>
     * 만약 장바구니에 하나 남은 메뉴에 대해서 삭제가 되면 빈 장바구니를 생성해주도록 한다.
     *
     * @param redisKey redis key
     * @param hashKey  메뉴 아이디
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
     * 모두 삭제 시 빈 장바구니를 생성해주도록 한다.
     *
     * @param redisKey redis key
     */
    public void removeCartMenuAll(String redisKey) {

        if (!cartRedisRepository.existKeyInCartRedis(redisKey)) {
            throw new NotFoundCartRedisKey();
        }

        cartRedisRepository.deleteCartAll(redisKey);
        createCartEmpty(redisKey, NO_MENU);
    }

    /**
     * Redis 장바구니에 redisKey 가 존재하는지 확인하는 메서드.
     *
     * @param redisKey redis Key
     * @return redis Key 존재여부를 반환
     */
    public boolean hasKeyInCartRedis(String redisKey) {

        return cartRedisRepository.existKeyInCartRedis(redisKey);
    }

    /**
     * Redis 장바구니에 redisKey 에 hashKey 존재하는지 확인하는 메서드.
     *
     * @param redisKey redis Key
     * @param menuKey  redis hashKey
     * @return redis Key 존재여부를 반환
     */
    public boolean hasMenuInCartRedis(String redisKey, String menuKey) {

        return cartRedisRepository.existMenuInCartRedis(redisKey, menuKey);
    }

    /**
     * Db 장바구니 정보를 Redis 로 저장하는 메서드.
     *
     * @param accountId 회원 아이디
     */
    public void createDbCartUploadRedis(String redisKey, Long accountId) {

        createAllCartFromDbToRedis(redisKey, cartRepository.lookupCartDbList(accountId));
    }

    private String makeFullPath(CartResponseDto cartResponseDto) {
        if (Objects.nonNull(cartResponseDto.getCartMenuResponseDto().getSavedName())) {
            FileUtils fileUtils =
                fileUtilResolver.getFileService(cartResponseDto.getCartMenuResponseDto().getLocationType());
            return fileUtils.getFullPath(cartResponseDto.getCartMenuResponseDto().getDomainName(),
                cartResponseDto.getCartMenuResponseDto().getSavedName());
        }
        return null;
    }

    /**
     * DB 장바구니에서 가져온 정보를 Redis 장바구니에 저장하는 메서드.
     *
     * @param cartResponseDtos DB 장바구니 정보
     */
    private void createAllCartFromDbToRedis(String redisKey, List<CartResponseDto> cartResponseDtos) {

        for (CartResponseDto cartResponseDto : cartResponseDtos) {
            CartMenuResponseDto cartMenuResponseDto = cartResponseDto.getCartMenuResponseDto();
            CartMenuDto cartMenuRedisDto =
                new CartMenuDto(cartMenuResponseDto.getMenuId(), cartMenuResponseDto.getName(),
                    makeFullPath(cartResponseDto), cartMenuResponseDto.getPrice());

            List<CartOptionDto> cartOptionRedisDtos = new ArrayList<>();
            if (!(cartResponseDto.getCartOptionResponseDto().size() == 1
                && cartResponseDto.getCartOptionResponseDto().get(0).getOptionId() == null)) {
                List<CartOptionResponseDto> cartOptionResponseDtos = cartResponseDto.getCartOptionResponseDto();

                for (CartOptionResponseDto cartOptionResponseDto : cartOptionResponseDtos) {
                    CartOptionDto cartOptionRedisDto =
                        new CartOptionDto(cartOptionResponseDto.getOptionId(),
                            cartOptionResponseDto.getName(), cartOptionResponseDto.getPrice());
                    cartOptionRedisDtos.add(cartOptionRedisDto);
                }
            }

            CartRedisDto cartRedisDto =
                new CartRedisDto(cartResponseDto.getAccountId(), cartResponseDto.getStoreId(),
                    cartResponseDto.getName(), cartResponseDto.getDeliveryCost(),
                    cartResponseDto.getMinimumOrderPrice(), cartMenuRedisDto, cartOptionRedisDtos,
                    cartMenuResponseDto.getCreateTimeMillis(), null,
                    cartMenuResponseDto.getCount(), null, null);

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
