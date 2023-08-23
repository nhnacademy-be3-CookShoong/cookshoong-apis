package store.cookshoong.www.cookshoongbackend.cart.db.service;

import static store.cookshoong.www.cookshoongbackend.cart.utils.CartConstant.LOCK;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.Cart;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetail;
import store.cookshoong.www.cookshoongbackend.cart.db.entity.CartDetailMenuOption;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartDetailMenuOptionRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartDetailRepository;
import store.cookshoong.www.cookshoongbackend.cart.db.repository.CartRepository;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartOptionDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.repository.CartRedisRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * Cart 에 대한 Service.
 *
 * @author jeongjewan
 * @since 2023.07.27
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartDetailMenuOptionRepository cartDetailMenuOptionRepository;
    private final CartDetailRepository cartDetailRepository;
    private final CartRepository cartRepository;
    private final CartRedisRepository cartRedisRepository;
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;


    /**
     * Redis Key 에 만료기간이 끝나면 Key 삭제 되기전 장바구니 데이터를 DB 에 저장하느 메서드.    <br>
     * 회원에 대한 장바구니 정보가 DB 에 있으면 삭제하고 새롭게 생성해준다.<br>
     * DB 가 생성이 되면 Lock 에 대한 키를 생성하고 이 키가 존재하지 않을 때만 생성한다. <br>
     * Cart 에 회원은 유니크 제약조건이 걸려 있으므로 중복으로 저장되지 않도록 DB 에 회원에 대한 카트가 존재하면 생성하지 못하도록 막는다 <br>
     *
     * @param cartRedisDtoList      해당 key 장바구니 내역
     */
    public void createCartDb(Long accountId, List<CartRedisDto> cartRedisDtoList) {
        if (!cartRedisRepository.existKeyInCartRedis(LOCK + accountId)) {
            validateCartInfoExistsForAccount(accountId, cartRedisDtoList);
        }
    }

    private void validateCartInfoExistsForAccount(Long accountId, List<CartRedisDto> cartRedisDtoList) {
        if (cartRepository.hasCartByAccountId(accountId)) {
            deleteCartDb(accountId);
        }
        saveDataOnRedisToDatabase(accountId, cartRedisDtoList);
    }

    private void saveDataOnRedisToDatabase(Long accountId, List<CartRedisDto> cartRedisList) {
        if (cartRedisList == null || cartRedisList.isEmpty()) {
            cartRedisRepository.createLockRedis(LOCK + accountId, LOCK);
            return;
        }

        if (cartRepository.hasCartByAccountId(accountId)) {
            return;
        }

        Cart cart = cartSave(cartRedisList);
        cartMenuSave(cartRedisList, cart);
        cartRedisRepository.createLockRedis(LOCK + accountId, LOCK);
    }

    private Cart cartSave(List<CartRedisDto> cartRedisList) {
        Account account =
            accountRepository.findById(cartRedisList.get(0).getAccountId()).orElseThrow(UserNotFoundException::new);
        Store store =
            storeRepository.findById(cartRedisList.get(0).getStoreId()).orElseThrow(StoreNotFoundException::new);

        Cart cart = new Cart(account, store);
        cartRepository.save(cart);
        return cart;
    }

    private void cartMenuSave(List<CartRedisDto> cartRedisList, Cart cart) {
        for (CartRedisDto cartRedisDto : cartRedisList) {
            Menu menu =
                menuRepository.findById(cartRedisDto.getMenu().getMenuId()).orElseThrow(MenuNotFoundException::new);
            CartDetail cartDetail =
                new CartDetail(cart, menu, cartRedisDto.getCount(), cartRedisDto.getCreateTimeMillis());
            cartDetailRepository.save(cartDetail);
            cartDetailOptionSave(cartRedisDto, cartDetail);
        }
    }

    private void cartDetailOptionSave(CartRedisDto cartRedisDto, CartDetail cartDetail) {
        List<CartOptionDto> optionDtos = cartRedisDto.getOptions();
        if (Objects.isNull(optionDtos)) {
            return;
        }
        for (CartOptionDto optionDto : optionDtos) {
            Option option =
                optionRepository.findById(optionDto.getOptionId()).orElseThrow(OptionNotFoundException::new);
            CartDetailMenuOption.Pk pk = new CartDetailMenuOption.Pk(cartDetail.getId(), option.getId());
            CartDetailMenuOption cartDetailMenuOption = new CartDetailMenuOption(pk, cartDetail, option);
            cartDetailMenuOptionRepository.save(cartDetailMenuOption);
        }
    }

    /**
     * 회원에 대한 DB 장바구니를 삭제하는 메서드.
     * Redis 장바구니가 DB 로 저장되기전에 사용
     *
     * @param accountId         회원아이디
     */
    public void deleteCartDb(Long accountId) {
        UUID cartId = cartRepository.findCartId(accountId);
        if (cartId == null) {
            return;
        }
        cartRepository.deleteById(cartId);
    }

    /**
     * 회원 DB 장바구니 존재하는 확인하는 메서드. <br>
     * 있으면 true, 없으면 false
     *
     * @param accountId         회원 아아디
     * @return                  있으면 true, 없으면 false 반환
     */
    public boolean hasCartByAccountId(Long accountId) {
        return cartRepository.hasCartByAccountId(accountId);
    }

}
