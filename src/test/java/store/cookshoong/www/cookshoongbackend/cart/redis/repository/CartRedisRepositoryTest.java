package store.cookshoong.www.cookshoongbackend.cart.redis.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.config.IntegrationTestBase;

/**
 * Redis 기능 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartRedisRepositoryTest extends IntegrationTestBase {

    @Autowired
    private CartRedisRepository cartRedisRepository;

    @Autowired
    private ObjectMapper om;

    String redisKey = "ded3e890-1c32-4dbc-bf35-55152b48c11d";
    String hashKey = "112";
    Long accountId = 1L;
    Long storeId = 2L;
    String storeName = "네네치킨";
    Integer deliveryCost = 3000;
    Integer minimumOrderPrice = 10000;
    Long menuId = 1L;
    int count = 1;

    @Test
    @Order(1)
    @DisplayName("Redis 서버에서 장바구니에 들어갈 메뉴 정보 담기")
    void addCartMenuRedis() {

        CartMenuDto cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", menuId);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "CHK");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", "menuImage");
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 3000);

        CartOptionDto cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 1L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "optionName2");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 2);

        CartOptionDto cartOptionDto1 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 2L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "optionName3");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 2000);

        List<CartOptionDto> cartOptionDtos = new ArrayList<>();
        cartOptionDtos.add(cartOptionDto);
        cartOptionDtos.add(cartOptionDto1);

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "deliveryCost", deliveryCost);
        ReflectionTestUtils.setField(cartRedisDto, "minimumOrderPrice", minimumOrderPrice);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", cartRedisDto.generateUniqueHashKey());
        ReflectionTestUtils.setField(cartRedisDto, "count", count);
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        cartRedisRepository.cartRedisSave(redisKey, hashKey, cartRedisDto);

        assertTrue(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey));
    }

    @Test
    @Order(2)
    @DisplayName("Redis 서버에서 들어가 있는 장바구니 전부 가져오기 - LIST")
    void getCartMenuRedisAll_List() throws Exception {

        List<Object> carts = cartRedisRepository.findByCartAll(redisKey);
        List<CartRedisDto> cartList = new ArrayList<>();

        for (Object cart : carts) {
            cartList.add((CartRedisDto) cart);
        }

        Comparator<CartRedisDto> sortCarts = Comparator.comparing(CartRedisDto::getCreateTimeMillis);

        cartList = cartList.stream().sorted(sortCarts).collect(Collectors.toList());

        assertNotNull(cartList);
        assertEquals(cartList.get(0).getAccountId(), accountId);
        assertEquals(cartList.get(0).getStoreId(), storeId);
        assertEquals(cartList.get(0).getStoreName(), storeName);
    }

    @Test
    @Order(3)
    @DisplayName("Redis 서버에서 들어가 있는 장바구니 특정 메뉴 가져오기")
    void getCartMenuRedis() {

        Object carts = cartRedisRepository.findByCartMenu(redisKey, hashKey);

        CartRedisDto cartRedisDto = (CartRedisDto) carts;

        assertNotNull(cartRedisDto);
        assertEquals(cartRedisDto.getAccountId(), accountId);
        assertEquals(cartRedisDto.getStoreId(), storeId);
        assertEquals(cartRedisDto.getStoreName(), storeName);
    }


    @Test
    @Order(4)
    @DisplayName("Redis 서버에서 장바구니에 들어가 있는 개수 가져오기")
    void getCartRedisCount() {

        Long count = cartRedisRepository.cartRedisSize(redisKey);

        assertNotNull(count);
        assertEquals(1, count);
    }

    @Test
    @Order(5)
    @DisplayName("Redis 서버에서 장바구니에서 변경된 메뉴 수정 하기, 업데이트전 메뉴 먼저 삭제 후 수정")
    void updateCartMenuRedis() {

        Long menuId = 1L;

        CartMenuDto cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", menuId);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "CHK");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", "menuImage");
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 1);

        CartOptionDto cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 3L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "MU");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 1);

        CartOptionDto cartOptionDto1 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 4L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "SAM");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 1);

        List<CartOptionDto> cartOptionDtos = new ArrayList<>();
        cartOptionDtos.add(cartOptionDto);
        cartOptionDtos.add(cartOptionDto1);

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);


        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "deliveryCost", deliveryCost);
        ReflectionTestUtils.setField(cartRedisDto, "minimumOrderPrice", minimumOrderPrice);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);

        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", cartRedisDto.generateUniqueHashKey());
        ReflectionTestUtils.setField(cartRedisDto, "count", count);
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        CartRedisDto deleteCartMenu = (CartRedisDto) cartRedisRepository.findByCartMenu(redisKey, "112");

        cartRedisRepository.cartMenuRedisModify(redisKey, cartRedisDto.getHashKey(), cartRedisDto);

        assertTrue(cartRedisRepository.existMenuInCartRedis(redisKey, cartRedisDto.getHashKey()));
    }

    @Test
    @Order(6)
    @DisplayName("Redis 서버에서 장바구니에 들어가 있는 메뉴가 존재하는 확인하는 메서드")
    void isMenuInCartRedis() {

        boolean is = cartRedisRepository.existMenuInCartRedis(redisKey, hashKey);

        assertTrue(is);
    }

    @Test
    @Order(7)
    @DisplayName("Redis 서버에서 장바구니키를 가져오는 메서드")
    void cartKeyInHashKey() {

        Set<Object> key = cartRedisRepository.cartKeyInHashKey(redisKey);

        assertNotNull(key);
    }

    @Test
    @Order(8)
    @DisplayName("Redis 서버에서 장바구니 카가 존재하는지 확인하는 매소드")
    void isKeyInCartRedis() {

        boolean is = cartRedisRepository.existKeyInCartRedis(redisKey);

        assertTrue(is);
    }

    @Test
    @Order(9)
    @DisplayName("Lock 에 사용되는 키 생성")
    void createLockRedis() {

        String lockKey = "lockKey=1";
        String hashKey = "lockHaskKey";

        cartRedisRepository.createLockRedis(lockKey, hashKey);

        Object cartRedis = cartRedisRepository.findByCartMenu(lockKey, hashKey);

        String lock = (String) cartRedis;

        assertNotNull(lock);
        assertTrue(cartRedisRepository.existMenuInCartRedis(lockKey, hashKey));
        assertEquals("lock", lock);
    }

    @Test
    @Order(10)
    @DisplayName("Redis 서버에서 장바구니에 들어가 있는 메뉴 삭제하기")
    void deleteCartMenuRedis() {

        cartRedisRepository.deleteCartMenu(redisKey, hashKey);

        assertFalse(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey));
    }

    @Test
    @Order(11)
    @DisplayName("Redis 서버에서 들어가 있는 key 삭제하기")
    void deleteCartMenuRedisAll() {

        cartRedisRepository.deleteCartAll(redisKey);

        assertFalse(cartRedisRepository.existKeyInCartRedis(redisKey));
    }

}
