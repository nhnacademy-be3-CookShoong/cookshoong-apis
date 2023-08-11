package store.cookshoong.www.cookshoongbackend.cart.redis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;

/**
 * Redis 장바구니에 대한 서비스 테스트 코드.
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
    @Mock
    private CartRepository cartRepository;
    @Mock
    private FileUtilResolver fileUtilResolver;
    @Mock
    private ObjectStorageService objectStorageService;

    String redisKey = "cartKey=1";
    String hashKey = "112";
    Long accountId = 1L;
    Long storeId = 1L;
    String storeName = "네네치킨";
    Long menuId = 1L;
    Integer count = 1;
    CartMenuDto cartMenuDto;
    CartOptionDto cartOptionDto;
    CartOptionDto cartOptionDto1;
    List<CartOptionDto> cartOptionDtos;
    private static final String NO_MENU = "NO_KEY";

    @BeforeEach
    void setup() {
        String menuImagePath = objectStorageService.getFullPath(FileDomain.MENU_IMAGE.getVariable(), UUID.randomUUID()+".png");
        cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", menuId);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "menuName2");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", menuImagePath);
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 3);
        ReflectionTestUtils.setField(cartMenuDto, "locationType", LocationType.OBJECT_S.getVariable());
        ReflectionTestUtils.setField(cartMenuDto, "domainName", FileDomain.MENU_IMAGE.getVariable());

        cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 1L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "optionName2");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 2000);

        cartOptionDto1 = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto1, "optionId", 2L);
        ReflectionTestUtils.setField(cartOptionDto1, "optionName", "optionName3");
        ReflectionTestUtils.setField(cartOptionDto1, "optionPrice", 1000);

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        cartRedisService.createCartMenu(redisKey, hashKey, cartRedisDto);

        verify(cartRedisRepository, times(1)).cartRedisSave(redisKey, hashKey, cartRedisDto);
    }

    @Test
    @DisplayName("장바구니에 추가하기 - 메뉴 장바구니에 담을 시 빈 장바구니가 존재하면 삭제")
    void createCartMenu_no_menu() {

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        when(cartRedisRepository.existMenuInCartRedis(redisKey, NO_MENU)).thenReturn(true);

        cartRedisService.createCartMenu(redisKey, hashKey, cartRedisDto);

        verify(cartRedisRepository).deleteCartMenu(redisKey, NO_MENU);
    }

    @Test
    @DisplayName("장바구니에 추가하기 - Redis 장바구니에 redisKey 와 hashKey 가 존재할 때")
    void createCartMenu_redisKeyAndHashKey_Exist() {

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        cartRedisDto.incrementCount();

        when(cartRedisRepository.existMenuInCartRedis(redisKey, NO_MENU)).thenReturn(false);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);
        when(cartRedisRepository.findByCartMenu(redisKey, hashKey)).thenReturn(cartRedisDto);

        cartRedisService.createCartMenu(redisKey, hashKey, cartRedisDto);

        verify(cartRedisRepository).findByCartMenu(redisKey, hashKey);
        verify(cartRedisRepository).cartRedisSave(eq(redisKey), eq(hashKey), any(CartRedisDto.class));
    }

    @Test
    @DisplayName("빈 장바구니 생성하기")
    void createCartEmpty() {

        cartRedisService.createCartEmpty(redisKey, NO_MENU);

        verify(cartRedisRepository).cartRedisSave(redisKey, NO_MENU, null);

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
        when(cartRedisRepository.existMenuInCartRedis(redisKey, NO_MENU)).thenReturn(false);
        when(cartRedisRepository.findByCartAll(redisKey)).thenReturn(List.of(cartRedisDto));

        // Verify exception
        assertThrows(InvalidStoreException.class,
            () -> cartRedisService.createCartMenu(redisKey, hashKey, cartRedisDto2));

        // Verify repository method calls
        verify(cartRedisRepository, times(1)).findByCartAll(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, NO_MENU);
        verify(cartRedisRepository, never()).cartRedisSave(any(), any(), any());
    }

    @Test
    @DisplayName("장바구니 메뉴 수정하기")
    void modifyCartMenuRedis() {

        String hashKeyModify = "134";

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);
        when(cartRedisRepository.findByCartMenu(redisKey, hashKey)).thenReturn(cartRedisDto);
        doNothing().when(cartRedisRepository).cartMenuRedisModify(redisKey, hashKey, cartRedisDto);

        cartRedisService.modifyCartMenuDecrementCount(redisKey, hashKey);

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository, times(1)).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).findByCartMenu(redisKey, hashKey);
        verify(cartRedisRepository, times(1)).cartMenuRedisModify(redisKey, hashKey, cartRedisDto);

        assertEquals(cartRedisDto.getCount(), 1);
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
    @DisplayName("Redis 장바구니에 들어있는 모든 메뉴 가져오기 - Redis 장바구니에 빈 장바구니가 존재할 때 빈 장바구니 조회")
    void selectCartMenuAll_List() {

//        when(cartRedisRepository.existMenuInCartRedis(redisKey, NO_MENU)).thenReturn(true);
        when(cartRedisRepository.findByCartMenu(redisKey, NO_MENU)).thenReturn(null);

        cartRedisService
            .selectCartMenuAll(redisKey);

//        verify(cartRedisRepository).existMenuInCartRedis(redisKey, NO_MENU);
        verify(cartRedisRepository).findByCartMenu(redisKey, NO_MENU);
    }

    @Test
    @DisplayName("Redis 장바구니에 들어있는 모든 메뉴 가져오기 - Redis에 데이터가 존재하지 않는 경우")
    void selectCartMenuAll_List_Redis_isEmpty() {
        String menuImagePath = objectStorageService.getFullPath(FileDomain.MENU_IMAGE.getVariable(), UUID.randomUUID()+".png");
        CartMenuResponseDto cartMenuResponseDto =
            new CartMenuResponseDto(menuId, "menuName2", menuImagePath, 3, System.currentTimeMillis(), 3,
                LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable());

        CartOptionResponseDto cartOptionResponseDto =
            new CartOptionResponseDto(1L, "optionName2", 2000);

        CartOptionResponseDto cartOptionResponseDto2 =
            new CartOptionResponseDto(2L, "optionName3", 1000);

        List<CartOptionResponseDto> cartOptionResponseDtoList = new ArrayList<>();
        cartOptionResponseDtoList.add(cartOptionResponseDto);
        cartOptionResponseDtoList.add(cartOptionResponseDto2);

        CartResponseDto cartResponseDto =
            ReflectionUtils.newInstance(CartResponseDto.class);

        ReflectionTestUtils.setField(cartResponseDto, "accountId", accountId);
        ReflectionTestUtils.setField(cartResponseDto, "storeId", storeId);
        ReflectionTestUtils.setField(cartResponseDto, "name", storeName);
        ReflectionTestUtils.setField(cartResponseDto, "cartMenuResponseDto", cartMenuResponseDto);
        ReflectionTestUtils.setField(cartResponseDto, "cartOptionResponseDto", cartOptionResponseDtoList);

        List carDBtList = new ArrayList();
        carDBtList.add(cartResponseDto);

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

        List cartRedisList = new ArrayList();
        cartRedisList.add(cartRedisDto);

        when(cartRedisRepository.existKeyInCartRedis(String.valueOf(accountId))).thenReturn(false);
        when(cartRepository.hasCartByAccountId(accountId)).thenReturn(true);
        when(cartRepository.lookupCartDbList(accountId)).thenReturn(carDBtList);
        when(cartRedisRepository.findByCartAll(String.valueOf(accountId))).thenReturn(cartRedisList);
        when(fileUtilResolver.getFileService(eq(LocationType.OBJECT_S.getVariable()))).thenReturn(objectStorageService);
        List<CartRedisDto> actual = cartRedisService.selectCartMenuAll(String.valueOf(accountId));

        verify(cartRedisRepository, times(1)).existKeyInCartRedis(String.valueOf(accountId));
        verify(cartRedisRepository, never()).findByCartMenu(String.valueOf(accountId), NO_MENU);
        verify(cartRepository, times(1)).lookupCartDbList(accountId);
        verify(cartRedisRepository, times(1)).findByCartAll(String.valueOf(accountId));

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
        ReflectionTestUtils.setField(cartRedisDto, "menuOptName", cartRedisDto.generateMenuOptionName());
        ReflectionTestUtils.setField(cartRedisDto, "totalMenuPrice", cartRedisDto.generateTotalMenuPrice());

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
    @DisplayName("Redis 장바구니에서 해당 메뉴를 삭제 - 장바구니에 메뉴가 하나 남아 있을 때 삭제되면 빈 장바구니 생성")
    void removeCartMenu_no_menu() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);
        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);
        when(cartRedisRepository.cartRedisSize(redisKey)).thenReturn(1L);

        cartRedisService.removeCartMenu(redisKey, hashKey);

        verify(cartRedisRepository).existKeyInCartRedis(redisKey);
        verify(cartRedisRepository).existMenuInCartRedis(redisKey, hashKey);
        verify(cartRedisRepository).deleteCartMenu(redisKey, hashKey);
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

    @Test
    @DisplayName("Redis 장바구니에 redisKey 가 존재하는지 확인")
    void existKeyInCartRedis() {

        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);

        boolean exists = cartRedisService.hasKeyInCartRedis(redisKey);

        verify(cartRedisRepository).existKeyInCartRedis(redisKey);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Redis 장바구니에 redisKey 에 hashKey 존재하는지 확인")
    void existMenuInCartRedis() {

        when(cartRedisRepository.existMenuInCartRedis(redisKey, hashKey)).thenReturn(true);

        boolean exists = cartRedisService.hasMenuInCartRedis(redisKey, hashKey);

        verify(cartRedisRepository).existMenuInCartRedis(redisKey, hashKey);

        assertTrue(exists);
    }

    @Test
    @DisplayName("DB 장바구니 정보를 Redis 로 저장")
    void createDbCartUploadRedis() {

        List<CartResponseDto> cartResponseDtos = new ArrayList<>();

        when(cartRepository.lookupCartDbList(accountId)).thenReturn(cartResponseDtos);

        cartRedisService.createDbCartUploadRedis(redisKey, accountId);

        verify(cartRepository).lookupCartDbList(accountId);
    }

    @Test
    @DisplayName("Redis 장바구니에서 모든 메뉴를 삭제")
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
