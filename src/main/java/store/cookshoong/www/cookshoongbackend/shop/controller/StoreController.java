package store.cookshoong.www.cookshoongbackend.shop.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponPolicyService;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreCategoryService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;

/**
 * 매장관련 컨트롤러 구현.
 *
 * @author seungyeon
 * @contributor eora21 (김주호)
 * @since 2023.07.05
 */
@RequestMapping("/api/stores")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final CouponPolicyService couponPolicyService;
    private final StoreCategoryService storeCategoryService;

    /**
     * 일반 유저 : 매장 정보 조회 페이지.
     *
     * @param storeId 매장 아이디
     * @return 매장 정보
     */
    @GetMapping("/{storeId}/info")
    public ResponseEntity<SelectStoreForUserResponseDto> getStoreInformation(
        @PathVariable("storeId") Long storeId,
        @RequestParam("addressId") Long addressId) {
        SelectStoreForUserResponseDto selectStoreForUserResponseDto = storeService.selectStoreForUser(addressId, storeId);

        selectStoreForUserResponseDto.setProvableCouponPolicies(
            couponPolicyService.getProvableStoreCouponPolicies(storeId));

        return ResponseEntity.ok(selectStoreForUserResponseDto);
    }

    /**
     * 일반 유저 : 모든 카테고리 조회.
     *
     * @return the store categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<SelectAllCategoriesResponseDto>> getStoreCategories() {
        return ResponseEntity
            .ok(storeCategoryService.selectAllCategoriesForUser());
    }
}
