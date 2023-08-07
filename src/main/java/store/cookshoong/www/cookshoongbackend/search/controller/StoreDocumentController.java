package store.cookshoong.www.cookshoongbackend.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
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
    private final ObjectStorageService objectStorageService;

    @GetMapping("/stores/search")
    public ResponseEntity<Page<StoreDocumentResponseDto>> searchByDistance(
        @RequestParam("addressId") Long addressId,
        Pageable pageable) {
        Page<StoreDocumentResponseDto> storeResponses
            = storeDocumentService.searchByDistance(addressId, pageable);

        storeResponses.forEach(
            storeDocumentResponseDto -> storeDocumentResponseDto.setSavedName(
                objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), storeDocumentResponseDto.getSavedName()))
        );

        storeResponses.forEach(
            storeDocumentResponseDto -> storeDocumentResponseDto.setCategories(
                storeCategoryService.selectCategoriesByStoreId(storeDocumentResponseDto.getId())
            )
        );

        return ResponseEntity.ok(storeResponses);
    }

    @GetMapping("/stores/search/keyword")
    public ResponseEntity<Page<StoreDocumentResponseDto>> searchByKeyword(
        @RequestParam("keyword") String keywordText,
        @RequestParam("addressId") Long addressId,
        Pageable pageable) {
        Page<StoreDocumentResponseDto> storeResponses
            = storeDocumentService.searchByKeywordText(keywordText, addressId, pageable);

        storeResponses.forEach(
            storeDocumentResponseDto -> storeDocumentResponseDto.setSavedName(
                objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), storeDocumentResponseDto.getSavedName()))
        );

        storeResponses.forEach(
            storeDocumentResponseDto -> storeDocumentResponseDto.setCategories(
                storeCategoryService.selectCategoriesByStoreId(storeDocumentResponseDto.getId())
            )
        );

        return ResponseEntity.ok(storeResponses);
    }
}
