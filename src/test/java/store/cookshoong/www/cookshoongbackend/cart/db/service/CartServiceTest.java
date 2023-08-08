package store.cookshoong.www.cookshoongbackend.cart.db.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.Cart;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetail;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartDetailMenuOptionRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartDetailRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartRepository;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartMenuDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.repository.CartRedisRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * DB 장바구니에 대한 서비스 테스트 코드.
 *
 * @author jeongjewan
 * @since 2023.07.30
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;
    @Mock
    private CartDetailMenuOptionRepository cartDetailMenuOptionRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartRedisRepository cartRedisRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OptionRepository optionRepository;

    String redisKey;
    List<CartRedisDto> cartRedisDtoList;

    @Test
    @DisplayName("DB 장바구니에 저장하기")
    void createCartDb() {
        redisKey = "cartKey=1";
        cartRedisDtoList = new ArrayList<>();

        CartOptionDto cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 1L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "SSAM");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 1000);
        List<CartOptionDto> cartOptionDtoList = new ArrayList<>();
        cartOptionDtoList.add(cartOptionDto);

        CartMenuDto cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", 1L);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "불난닭?");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", "menu_1.jpg");
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 19000);

        CartRedisDto cartRedisDto = ReflectionUtils.newInstance(CartRedisDto.class);
        ReflectionTestUtils.setField(cartRedisDto, "accountId", 1L);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", 1L);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", "어반");
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtoList);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", 1L);
        ReflectionTestUtils.setField(cartRedisDto, "count", 1);

        cartRedisDtoList.add(cartRedisDto);

        Account account = ReflectionUtils.newInstance(Account.class);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Store store = ReflectionUtils.newInstance(Store.class);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        Menu menu = ReflectionUtils.newInstance(Menu.class);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        Option option = ReflectionUtils.newInstance(Option.class);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);

        assertDoesNotThrow(() -> cartService.createCartDb(redisKey, cartRedisDtoList));

        verify(cartRepository).save(any(Cart.class));
        verify(cartDetailRepository).save(any(CartDetail.class));
        verify(cartDetailMenuOptionRepository).save(any(CartDetailMenuOption.class));
    }



    @Test
    @DisplayName("DB 장바구니에 저장하기 - DB 에 회원 장바구니 존재시 삭제 후 저장")
    void createCartDb_delete() {
        redisKey = "cartKey=1";
        cartRedisDtoList = new ArrayList<>();

        CartOptionDto cartOptionDto = ReflectionUtils.newInstance(CartOptionDto.class);
        ReflectionTestUtils.setField(cartOptionDto, "optionId", 1L);
        ReflectionTestUtils.setField(cartOptionDto, "optionName", "SSAM");
        ReflectionTestUtils.setField(cartOptionDto, "optionPrice", 1000);
        List<CartOptionDto> cartOptionDtoList = new ArrayList<>();
        cartOptionDtoList.add(cartOptionDto);

        CartMenuDto cartMenuDto = ReflectionUtils.newInstance(CartMenuDto.class);
        ReflectionTestUtils.setField(cartMenuDto, "menuId", 1L);
        ReflectionTestUtils.setField(cartMenuDto, "menuName", "불난닭?");
        ReflectionTestUtils.setField(cartMenuDto, "menuImage", "menu_1.jpg");
        ReflectionTestUtils.setField(cartMenuDto, "menuPrice", 19000);

        CartRedisDto cartRedisDto = ReflectionUtils.newInstance(CartRedisDto.class);
        ReflectionTestUtils.setField(cartRedisDto, "accountId", 1L);
        ReflectionTestUtils.setField(cartRedisDto, "storeId", 1L);
        ReflectionTestUtils.setField(cartRedisDto, "storeName", "어반");
        ReflectionTestUtils.setField(cartRedisDto, "menu", cartMenuDto);
        ReflectionTestUtils.setField(cartRedisDto, "options", cartOptionDtoList);
        ReflectionTestUtils.setField(cartRedisDto, "createTimeMillis", 1L);
        ReflectionTestUtils.setField(cartRedisDto, "count", 1);

        cartRedisDtoList.add(cartRedisDto);

        Account account = ReflectionUtils.newInstance(Account.class);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Store store = ReflectionUtils.newInstance(Store.class);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        Menu menu = ReflectionUtils.newInstance(Menu.class);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        Option option = ReflectionUtils.newInstance(Option.class);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(cartRedisRepository.existKeyInCartRedis(redisKey)).thenReturn(true);

        assertDoesNotThrow(() -> cartService.createCartDb(redisKey, cartRedisDtoList));

        verify(cartRepository).save(any(Cart.class));
        verify(cartDetailRepository).save(any(CartDetail.class));
        verify(cartDetailMenuOptionRepository).save(any(CartDetailMenuOption.class));
    }

    @Test
    @DisplayName("DB 장바구니 삭제하기")
    void deleteCartDb() {

        Long accountId = 1L;
        UUID cartId = UUID.randomUUID();

        when(cartRepository.findCartId(accountId)).thenReturn(cartId);

        assertDoesNotThrow(() -> cartService.deleteCartDb(accountId));

        verify(cartRepository).findCartId(accountId);
        verify(cartRepository).deleteById(cartId);
    }

    @Test
    @DisplayName("DB 장바구니 존재여부 확인하기")
    void hasCartByAccountId() {

        Long accountId = 1L;

        when(cartRepository.hasCartByAccountId(accountId)).thenReturn(true);

        assertTrue(cartService.hasCartByAccountId(accountId));

        verify(cartRepository).hasCartByAccountId(accountId);
    }
}
