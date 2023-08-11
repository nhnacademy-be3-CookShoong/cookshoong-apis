package store.cookshoong.www.cookshoongbackend.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponPolicyService;
import store.cookshoong.www.cookshoongbackend.coupon.service.IssueCouponService;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentResponseDto;
import store.cookshoong.www.cookshoongbackend.search.service.StoreDocumentService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreCategoryService;

/**
 * 매장 도큐먼트 컨트롤러.
 *
 * @author papel
 * @since 2023.07.20
 */
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class StoreDocumentController {
    private final StoreDocumentService storeDocumentService;
    private final StoreCategoryService storeCategoryService;
    private final FileUtilResolver fileUtilResolver;
    private final CouponPolicyService couponPolicyService;


    @GetMapping("/stores/search")
    public ResponseEntity<Page<StoreDocumentResponseDto>> searchByDistance(
        @RequestParam("addressId") Long addressId,
        Pageable pageable) {
        Page<StoreDocumentResponseDto> storeResponses
            = storeDocumentService.searchByDistance(addressId, pageable);

        updateStoreInfo(storeResponses);

        return ResponseEntity.ok(storeResponses);
    }

    private void updateStoreInfo(Page<StoreDocumentResponseDto> storeResponses) {
        for (StoreDocumentResponseDto s : storeResponses) {
            FileUtils fileUtils = fileUtilResolver.getFileService(s.getLocationType());
            s.setSavedName(fileUtils.getFullPath(s.getDomainName(), s.getSavedName()));
            s.setOfferCoupon(couponPolicyService.isOfferCouponInStore(s.getId()));
        }

        storeResponses.forEach(
            storeDocumentResponseDto -> storeDocumentResponseDto.setCategories(
                storeCategoryService.selectCategoriesByStoreId(storeDocumentResponseDto.getId())
            )
        );
    }

    @GetMapping("/stores/search/keyword")
    public ResponseEntity<Page<StoreDocumentResponseDto>> searchByKeyword(
        @RequestParam("keyword") String keywordText,
        @RequestParam("addressId") Long addressId,
        Pageable pageable) {
        Page<StoreDocumentResponseDto> storeResponses
            = storeDocumentService.searchByKeywordText(keywordText, addressId, pageable);

        updateStoreInfo(storeResponses);

        return ResponseEntity.ok(storeResponses);
    }
}
