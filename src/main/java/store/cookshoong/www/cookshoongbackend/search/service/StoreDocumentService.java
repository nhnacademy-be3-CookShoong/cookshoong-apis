package store.cookshoong.www.cookshoongbackend.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocument;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocumentResponseDto;
import store.cookshoong.www.cookshoongbackend.search.repository.StoreSearchRepository;

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

    public Page<StoreDocumentResponseDto> searchByKeywordText(String keyword, Long addressId, Pageable pageable) {
        Page<StoreDocument> storeDocuments = storeSearchRepository.searchByKeywordText(keyword, addressId, pageable);
        return storeDocuments.map(StoreDocumentResponseDto::from);
    }

    public Page<StoreDocumentResponseDto> searchByDistance(Long addressId, Pageable pageable) {
        Page<StoreDocument> storeDocuments = storeSearchRepository.searchByDistance(addressId, pageable);
        return storeDocuments.map(StoreDocumentResponseDto::from);
    }
}
