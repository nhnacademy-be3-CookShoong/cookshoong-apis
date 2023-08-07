package store.cookshoong.www.cookshoongbackend.search.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocument;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentRequestAllDto;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentResponseDto;
import store.cookshoong.www.cookshoongbackend.search.repository.StoreSearchRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;

/**
 * 매장 도큐먼트 서비스.
 *
 * @author papel
 * @since 2023.07.21
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreDocumentService {
    private final StoreSearchRepository storeSearchRepository;
    private final StoreCategoryRepository storesCategoryRepository;

    @Transactional
    public void saveAll(StoreDocumentRequestAllDto storeDocumentRequestAllDto) {
        List<StoreDocument> storeList =
            storeDocumentRequestAllDto.getStoreDocumentRequestDtoList().stream().map(StoreDocument::from).collect(Collectors.toList());
        storeSearchRepository.saveAll(storeList);
    }

    public Page<StoreDocumentResponseDto> searchByKeywordText(String keyword, Long addressId, Pageable pageable) {
        Page<StoreDocument> storeDocuments = storeSearchRepository.searchByKeywordText(keyword, addressId, pageable);
        return storeDocuments.map(StoreDocumentResponseDto::from);
    }

    public Page<StoreDocumentResponseDto> searchByDistance(Long addressId, Pageable pageable) {
        Page<StoreDocument> storeDocuments = storeSearchRepository.searchByDistance(addressId, pageable);
        return storeDocuments.map(StoreDocumentResponseDto::from);
    }
}
