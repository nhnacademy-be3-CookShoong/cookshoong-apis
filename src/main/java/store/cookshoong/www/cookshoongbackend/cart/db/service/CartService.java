package store.cookshoong.www.cookshoongbackend.cart.db.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
import store.cookshoong.www.cookshoongbackend.lock.LockProcessor;
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
    private final LockProcessor lockProcessor;
    private static final String CART = "cartKey=";

    /**
     * 동시성 문제를 해결하기 위해 분산락을 사용하는 메서드.
     *
     * @param redisKey          redis key
     * @param cartRedisList     장바구니 목록
     */
    public void createCartDbWithRock(String redisKey, List<CartRedisDto> cartRedisList) {

        lockProcessor.lock(redisKey, ignore -> createCartDb(redisKey, cartRedisList));
    }

    /**
     * Redis Key 에 만료기간이 끝나면 Key 삭제 되기전 장바구니 데이터를 DB 에 저장하느 메서드.    <br>
     * 회원에 대한 장바구니 정보가 DB 에 있으면 삭제하고 새롭게 생성해준다.
     *
     * @param redisKey              장바구니 key
     * @param cartRedisDtoList      해당 key 장바구니 내역
     */
    public void createCartDb(String redisKey, List<CartRedisDto> cartRedisDtoList) {

        if (cartRedisRepository.existKeyInCartRedis(redisKey)) {
            updateRedisCartKey(redisKey, cartRedisDtoList);
        }
    }

    private void updateRedisCartKey(String accountId, List<CartRedisDto> cartRedisList) {

        String id = accountId.replaceAll(CART, "");

        // DB 에 회원에 대한 장바구니
        deleteCartDb(Long.valueOf(id));

        if (cartRedisList == null || cartRedisList.isEmpty()) {
            return;
        }

        Account account =
            accountRepository.findById(cartRedisList.get(0).getAccountId()).orElseThrow(UserNotFoundException::new);
        Store store =
            storeRepository.findById(cartRedisList.get(0).getStoreId()).orElseThrow(StoreNotFoundException::new);

        Cart cart = new Cart(account, store);
        cartRepository.save(cart);

        for (CartRedisDto cartRedisDto : cartRedisList) {

            Menu menu =
                menuRepository.findById(cartRedisDto.getMenu().getMenuId()).orElseThrow(MenuNotFoundException::new);

            CartDetail cartDetail =
                new CartDetail(cart, menu, cartRedisDto.getCount(), cartRedisDto.getCreateTimeMillis());

            cartDetailRepository.save(cartDetail);
            List<CartOptionDto> optionDtos = cartRedisDto.getOptions();

            if (optionDtos != null) {
                for (CartOptionDto optionDto : optionDtos) {
                    Option option =
                        optionRepository.findById(optionDto.getOptionId()).orElseThrow(OptionNotFoundException::new);

                    CartDetailMenuOption.Pk pk = new CartDetailMenuOption.Pk(cartDetail.getId(), option.getId());
                    CartDetailMenuOption cartDetailMenuOption = new CartDetailMenuOption(pk, cartDetail, option);
                    cartDetailMenuOptionRepository.save(cartDetailMenuOption);
                }
            }
        }

        if (cartRedisRepository.existKeyInCartRedis(accountId)) {
            cartRedisRepository.deleteCartAll(accountId);
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
