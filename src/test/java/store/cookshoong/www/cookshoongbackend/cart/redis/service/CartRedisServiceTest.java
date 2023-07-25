package store.cookshoong.www.cookshoongbackend.cart.redis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.InvalidStoreException;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundCartRedisKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.exception.NotFoundMenuRedisHashKey;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.repository.CartRedisRepository;

/**
 * Redis 장바구니에 대한 서버스 테스트 코드.
 *
 * @author jeongjewan
 * @since 2023.07.23
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class CartRedisServiceTest {

    @InjectMocks
    private CartRedisService cartRedisService;

    @Mock
    private CartRedisRepository cartRedisRepository;

    String redisKey = "cart_account:1";
    String hashKey = "1:1,2";
    Long accountId = 1L;
    Long storeId = 1L;
    String storeName = "네네치킨";
    Long menuId = 1L;
    int count = 1;
    CartMenuDto cartMenuDto;
    CartOptionDto cartOptionDto;
    CartOptionDto cartOptionDto1;
    List<CartOptionDto> cartOptionDtos;

    @BeforeEach
    void setup() {
        cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", menuId);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "menuName2");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", "menuImage");
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 3);

        cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 1L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "optionName2");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 2);

        cartOptionDto1 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 2L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "optionName3");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 1);

        cartOptionDtos = new ArrayList<>();
        cartOptionDtos.add(cartOptionDto);
        cartOptionDtos.add(cartOptionDto1);
    }

    @Test
    @DisplayName("장바구니에 추가하기")
    void createCartMenu() {
        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);

        cartRedisService.createCartMenu(redisKey, hashKey, cartRedisDto);

        verify(cartRedisRepository, times(1)).cartRedisSave(redisKey, hashKey, cartRedisDto);
    }

    @Test
    @DisplayName("장바구니에 추가하기 실패 - 다른 매장에 대해서 추가하는 경우")
    void createCartMenu_InvalidStoreException() {

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);
        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);

        CartRedisDto cartRedisDto2 =
            ReflectionUtils.newInstance(CartRedisDto.class);
        ReflectionTestUtils.setField(cartRedisDto2, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto2, "storeId", 2L);

        // Mock behavior
        when(cartRedisRepository.findByCartAll(redisKey)).thenReturn(List.of(cartRedisDto));

        // Verify exception
        assertThrows(InvalidStoreException.class,
            () -> cartRedisService.createCartMenu(redisKey, hashKey, cartRedisDto2));

        // Verify repository method calls
        verify(cartRedisRepository, times(1)).findByCartAll(redisKey);
        verify(cartRedisRepository, never()).existMenuInCartRedis(any(), any());
        verify(cartRedisRepository, never()).cartRedisSave(any(), any(), any());
    }

    @Test
    @DisplayName("장바구니 메뉴 수정하기")
    void modifyCartMenuRedis() {

        String hashKeyModify = "1:3,4";

        CartOptionDto cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 3L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "MU");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 1);

        CartOptionDto cartOptionDto1 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 4L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "SAM");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 1);

        List<CartOptionDto> modifyOptions = new ArrayList<>();
        modifyOptions.add(cartOptionDto);
        modifyOptions.add(cartOptionDto1);

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", modifyOptions);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);

        cartRedisService.modifyCartMenuRedis(redisKey, hashKey, cartRedisDto);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).deleteCartMenu(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).cartMenuRedisModify(redisKey, hashKeyModify, cartRedisDto);

        assertEquals(cartRedisDto.getOptions().get(0).getOptionName(), cartOptionDto.getOptionName());
        assertEquals(cartRedisDto.getOptions().get(0).getOptionPrice(), cartOptionDto.getOptionPrice());
        assertEquals(cartRedisDto.getOptions().get(0).getOptionId(), cartOptionDto.getOptionId());
        assertEquals(cartRedisDto.getOptions().get(1).getOptionName(), cartOptionDto1.getOptionName());
        assertEquals(cartRedisDto.getOptions().get(1).getOptionPrice(), cartOptionDto1.getOptionPrice());
        assertEquals(cartRedisDto.getOptions().get(1).getOptionId(), cartOptionDto1.getOptionId());
    }

    @Test
    @DisplayName("장바구니 메뉴 수정 실패 - 존재하지 않는 Redis key 로 수정하는 경우")
    void modifyCartMenuRedis_NotFoundCartRedisKey() {

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        // Mock behavior
        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        // Verify exception
        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.modifyCartMenuRedis(redisKey, hashKey, cartRedisDto));

        // Verify repository method calls
        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, never()).existMenuInCartRedis(any(), any());
        verify(cartRedisRepository, never()).cartMenuRedisModify(any(), any(), any());
    }

    @Test
    @DisplayName("장바구니에 담아져 있는 메뉴에 수량을 늘리는 메서드")
    void modifyCartMenuIncrementCount() {
        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);
        when(cartRedisRepository.findByCartMenu(redisKey, hashKey)).thenReturn(cartRedisDto);
        doNothing().when(cartRedisRepository).cartMenuRedisModify(redisKey, hashKey, cartRedisDto);

        cartRedisService.modifyCartMenuIncrementCount(redisKey, hashKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).findByCartMenu(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).cartMenuRedisModify(redisKey, hashKey, cartRedisDto);

        assertEquals(cartRedisDto.getCount(), 2);
    }

    @Test
    @DisplayName("장바구니에 담아져 있는 메뉴에 수량을 늘리는 메서드 실패 - 존재하지 않는 Redis key 로 수정하는 경우")
    void modifyCartMenuIncrementCount_NotFoundCartRedisKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.modifyCartMenuIncrementCount(redisKey, hashKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, never()).existMenuInCartRedis(any(), any());
        verify(cartRedisRepository, never()).cartMenuRedisModify(any(), any(), any());
    }

    @Test
    @DisplayName("장바구니에 담아져 있는 메뉴에 수량을 줄이는 메서드")
    void modifyCartMenuDecrementCount() {
        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);
        when(cartRedisRepository.findByCartMenu(redisKey, hashKey)).thenReturn(cartRedisDto);
        doNothing().when(cartRedisRepository).cartMenuRedisModify(redisKey, hashKey, cartRedisDto);

        cartRedisService.modifyCartMenuDecrementCount(redisKey, hashKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).findByCartMenu(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).cartMenuRedisModify(redisKey, hashKey, cartRedisDto);

        assertEquals(cartRedisDto.getCount(), 0);
    }

    @Test
    @DisplayName("장바구니에 담아져 있는 메뉴에 수량을 늘리는 메서드 실패 - 존재하지 않는 Redis key 로 수정하는 경우")
    void modifyCartMenuDecrementCount_NotFoundCartRedisKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.modifyCartMenuDecrementCount(redisKey, hashKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, never()).existMenuInCartRedis(any(), any());
        verify(cartRedisRepository, never()).cartMenuRedisModify(any(), any(), any());
    }


    @Test
    @DisplayName("Redis 장바구니에 들어있는 모든 메뉴 가져오기")
    void selectCartMenuAll_List() {

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);

        List carts = new ArrayList();
        carts.add(cartRedisDto);

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.findByCartAll(redisKey)).thenReturn(carts);

        List<CartRedisDto> actual = cartRedisService.selectCartMenuAll(redisKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).findByCartAll(redisKey);

        assertNotNull(actual);
        assertEquals(actual.get(0).getStoreId(), storeId);
        assertEquals(actual.get(0).getStoreName(), storeName);
        assertEquals(actual.get(0).getMenu().getMenuId(), cartMenuDto.getMenuId());
        assertEquals(actual.get(0).getMenu().getMenuImage(), cartMenuDto.getMenuImage());
        assertEquals(actual.get(0).getMenu().getMenuPrice(), cartMenuDto.getMenuPrice());
        assertEquals(actual.get(0).getMenu().getMenuName(), cartMenuDto.getMenuName());
        assertEquals(actual.get(0).getOptions().get(0).getOptionId(), cartOptionDto.getOptionId());
        assertEquals(actual.get(0).getOptions().get(0).getOptionPrice(), cartOptionDto.getOptionPrice());
        assertEquals(actual.get(0).getOptions().get(0).getOptionName(), cartOptionDto.getOptionName());
        assertEquals(actual.get(0).getOptions().get(1).getOptionId(), cartOptionDto1.getOptionId());
        assertEquals(actual.get(0).getOptions().get(1).getOptionPrice(), cartOptionDto1.getOptionPrice());
        assertEquals(actual.get(0).getOptions().get(1).getOptionName(), cartOptionDto1.getOptionName());
    }

    @Test
    @DisplayName("Redis 장바구니에 들어있는 모든 메뉴 가져오기 실패 - 장바구니 key 가 존재하지 않을 때")
    void selectCartMenuAll_NotFoundCartRedisKey() {

        // Mock behavior
        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        // Verify exception
        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.selectCartMenuAll(redisKey));

        // Verify repository method calls
        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
    }

    @Test
    @DisplayName("Redis 장바구니에 해당 메뉴를 조회")
    void selectCartMenu() {

        CartRedisDto cartRedisDto =
            ReflectionUtils.newInstance(CartRedisDto.class);

        ReflectionTestUtils.setField(cartRedisDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", storeName);
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtos);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", System.currentTimeMillis());
        ReflectionTestUtils.setField(cartRedisDto, "hashKey", hashKey);
        ReflectionTestUtils.setField(cartRedisDto, "count", count);

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);
        when(cartRedisRepository.findByCartMenu(redisKey, hashKey)).thenReturn(cartRedisDto);

        CartRedisDto actual = cartRedisService.selectCartMenu(redisKey, hashKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).findByCartMenu(redisKey, hashKey);

        assertNotNull(actual);
        assertEquals(actual.getStoreId(), storeId);
        assertEquals(actual.getStoreName(), storeName);
        assertEquals(actual.getMenu().getMenuId(), cartMenuDto.getMenuId());
        assertEquals(actual.getMenu().getMenuImage(), cartMenuDto.getMenuImage());
        assertEquals(actual.getMenu().getMenuPrice(), cartMenuDto.getMenuPrice());
        assertEquals(actual.getMenu().getMenuName(), cartMenuDto.getMenuName());
        assertEquals(actual.getOptions().get(0).getOptionId(), cartOptionDto.getOptionId());
        assertEquals(actual.getOptions().get(0).getOptionPrice(), cartOptionDto.getOptionPrice());
        assertEquals(actual.getOptions().get(0).getOptionName(), cartOptionDto.getOptionName());
        assertEquals(actual.getOptions().get(1).getOptionId(), cartOptionDto1.getOptionId());
        assertEquals(actual.getOptions().get(1).getOptionPrice(), cartOptionDto1.getOptionPrice());
        assertEquals(actual.getOptions().get(1).getOptionName(), cartOptionDto1.getOptionName());
    }

    @Test
    @DisplayName("Redis 장바구니에 해당 메뉴를 조회 실패 - 장바구니 key 가 존재하지 않을 때")
    void selectCartMenu_NotFoundCartRedisKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.selectCartMenu(redisKey, hashKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, never()).existMenuInCartRedis(any(), any());
    }

    @Test
    @DisplayName("Redis 장바구니에 해당 메뉴를 조회 실패 - 장바구니 key 가 존재하지 않을 때")
    void selectCartMenu_NotFoundMenuRedisHashKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(false);

        assertThrows(NotFoundMenuRedisHashKey.class,
            () -> cartRedisService.selectCartMenu(redisKey, hashKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
    }

    @Test
    @DisplayName("Redis 장바구니에 해당 메뉴 에 개수를 조회")
    void selectCartCount() {
        Long size = 3L;

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.cartRedisSize(redisKey)).thenReturn(size);

        Long cartSize = cartRedisService.selectCartCount(redisKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).cartRedisSize(redisKey);
        assertNotNull(cartSize);
        assertEquals(cartSize, size);
    }

    @Test
    @DisplayName("Redis 장바구니에 해당 메뉴 에 개수를 조회 실패 - 장바구니 key 가 존재하지 않을 때")
    void selectCartCount_NotFoundCartRedisKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.selectCartCount(redisKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
    }

    @Test
    @DisplayName("Redis 장바구니에서 해당 메뉴를 삭제")
    void removeCartMenu() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);

        cartRedisService.removeCartMenu(redisKey, hashKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).deleteCartMenu(redisKey, hashKey);
    }

    @Test
    @DisplayName("Redis 장바구니에서 해당 메뉴를 삭제 실패 - 장바구니 key 가 존재하지 않을 때")
    void removeCartMenu_NotFoundCartRedisKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.removeCartMenu(redisKey, hashKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, never()).existMenuInCartRedis(any(), any());
    }



    @Test
    @DisplayName("Redis 장바구니에서 해당 메뉴를 삭제 실패 - 장바구니 key 에 해당 메뉴가 존재하지 않을 때")
    void removeCartMenu_NotFoundMenuRedisHashKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(false);

        assertThrows(NotFoundMenuRedisHashKey.class,
            () -> cartRedisService.removeCartMenu(redisKey, hashKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
    }

    @DisplayName("Redis 장바구니에서 모든 메뉴를 삭제")
    @Test
    void removeCartMenuAll() {

        String redisKey = "key";

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);

        cartRedisService.removeCartMenuAll(redisKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).deleteCartAll(redisKey);
    }

    @Test
    @DisplayName("Redis 장바구니에서 모든 메뉴를 삭제 실패 - 장바구니 key 가 존재하지 않을 때")
    void removeCartMenuAll_NotFoundCartRedisKey() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(false);

        assertThrows(NotFoundCartRedisKey.class,
            () -> cartRedisService.removeCartMenuAll(redisKey));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
    }
}
