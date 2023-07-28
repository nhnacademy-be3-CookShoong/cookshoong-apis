package store.cookshoong.www.cookshoongbackend.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentRequestAllDto;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentResponseDto;
import store.cookshoong.www.cookshoongbackend.search.service.StoreDocumentService;

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

    @PutMapping("/store/keyword")
    public void saveByKeyword(@RequestBody StoreDocumentRequestAllDto storeDocumentRequestAllDto) {
        storeDocumentService.saveAll(storeDocumentRequestAllDto);
    }

    @GetMapping("/store/search")
    public ResponseEntity<Page<StoreDocumentResponseDto>> searchByKeyword(
        @RequestParam("keyword") String keywordText, Pageable pageable) {
        Page<StoreDocumentResponseDto> storeResponses
            = storeDocumentService.searchByKeywordText(keywordText, pageable);
        return ResponseEntity.ok(storeResponses);
    }
}
