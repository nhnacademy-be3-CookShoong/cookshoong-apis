package store.cookshoong.www.cookshoongbackend.cart.db.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.cart.db.service.CartService;

/**
 * DB 장바구니에 대한 Controller.
 *
 * @author jeongjewan
 * @since 2023.07.29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    /**
     * DB 장바구니 존재 여부를 반환.
     *
     * @param accountId         회원 아아디
     * @return                  장바구니 존재여부 반환
     */
    @GetMapping("/db/{accountId}/has")
    public ResponseEntity<Boolean> getHasDbCart(@PathVariable Long accountId) {

        Boolean isCart = cartService.hasCartByAccountId(accountId);

        return ResponseEntity.ok(isCart);
    }
}
