package store.cookshoong.www.cookshoongbackend.order.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;
import store.cookshoong.www.cookshoongbackend.cart.redis.model.vo.CartRedisDto;
import store.cookshoong.www.cookshoongbackend.cart.redis.service.CartRedisService;
import store.cookshoong.www.cookshoongbackend.coupon.entity.IssueCoupon;
import store.cookshoong.www.cookshoongbackend.coupon.service.IssueCouponService;
import store.cookshoong.www.cookshoongbackend.coupon.service.ProvideCouponService;
import store.cookshoong.www.cookshoongbackend.order.exception.OutOfDistanceException;
import store.cookshoong.www.cookshoongbackend.order.model.request.CreateOrderRequestDto;
import store.cookshoong.www.cookshoongbackend.order.service.OrderService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;

/**
 * 주문 관련 controller.
 *
 * @author eora21 (김주호)
 * @since 2023.08.02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final CartRedisService cartRedisService;
    private final IssueCouponService issueCouponService;
    private final ProvideCouponService provideCouponService;
    private final AddressService addressService;
    private final StoreService storeService;

    /**
     * 주문 생성 엔드포인트.
     *
     * @param createOrderRequestDto the create order request dto
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Void> postOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
        validOrderDistance(createOrderRequestDto);

        List<CartRedisDto> cartItems =
            cartRedisService.selectCartMenuAll(CartRedisService.CART + createOrderRequestDto.getAccountId());

        validIssueCouponInOrder(createOrderRequestDto, cartItems);

        orderService.createOrder(createOrderRequestDto, cartItems);

        return ResponseEntity.status(HttpStatus.CREATED)
            .build();
    }

    private void validOrderDistance(CreateOrderRequestDto createOrderRequestDto) {
        AddressResponseDto addressResponseDto =
            addressService.selectAccountAddressRenewalAt(createOrderRequestDto.getAccountId());

        boolean inStandardDistance =
            storeService.isInStandardDistance(addressResponseDto, createOrderRequestDto.getStoreId());

        if (!inStandardDistance) {
            throw new OutOfDistanceException();
        }
    }

    private void validIssueCouponInOrder(CreateOrderRequestDto createOrderRequestDto, List<CartRedisDto> cartItems) {
        IssueCoupon issueCoupon =
            issueCouponService.selectIssueCouponByCode(createOrderRequestDto.getIssueCouponCode());
        provideCouponService.validProvideCoupon(issueCoupon, createOrderRequestDto.getAccountId());

        int totalPrice = cartRedisService.getTotalPrice(cartItems);
        provideCouponService.validMinimumOrderPrice(issueCoupon, totalPrice);

        provideCouponService.validExpirationDateTime(issueCoupon);
    }
}
