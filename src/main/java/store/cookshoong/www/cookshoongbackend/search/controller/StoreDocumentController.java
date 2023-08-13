package store.cookshoong.www.cookshoongbackend.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.coupon.service.CouponPolicyService;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentResponseDto;
import store.cookshoong.www.cookshoongbackend.search.service.StoreDocumentService;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.service.BusinessHourService;
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
    private final BusinessHourService businessHourService;

    /**
     * 사용자로부터 3Km 내에 존재하는 매장들을 들고오는 컨트롤러.
     *
     * @param addressId the address id
     * @param pageable  the pageable
     * @return the response entity
     */
    @GetMapping("/stores/search")
    public ResponseEntity<Page<StoreDocumentResponseDto>> searchByDistance(
        @RequestParam("addressId") Long addressId,
        Pageable pageable) {
        Page<StoreDocumentResponseDto> storeResponses
            = storeDocumentService.searchByDistance(addressId, pageable);

        updateStoreInfo(storeResponses);

        return ResponseEntity.ok(storeResponses);
    }

    /**
     * 검색한 키워드에 해당되는 매장을 들고오는 컨트롤러.
     *
     * @param keywordText the keyword text
     * @param addressId   the address id
     * @param pageable    the pageable
     * @return the response entity
     */
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

    private void updateStoreInfo(Page<StoreDocumentResponseDto> storeResponses) {
        for (StoreDocumentResponseDto s : storeResponses) {
            if (!s.getStoreStatus().equals(StoreStatus.StoreStatusCode.CLOSE.name())) {
                Store newStore = businessHourService.updateStoreStatusByTimer(s.getId());
                s.setStoreStatus(newStore.getStoreStatus().getCode());
            }
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
}
