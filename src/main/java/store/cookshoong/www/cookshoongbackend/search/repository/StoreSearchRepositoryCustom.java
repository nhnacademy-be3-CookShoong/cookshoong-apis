package store.cookshoong.www.cookshoongbackend.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocument;

/**
 * 매장 도큐먼트 레포지토리 커스텀.
 *
 * @author papel
 * @since 2023.07.21
 */
public interface StoreSearchRepositoryCustom {

    Page<StoreDocument> searchByKeywordText(String keywordText, Long addressId, Pageable pageable);

    Page<StoreDocument> searchByDistance(Long addressId, Pageable pageable);

    Page<StoreDocument> searchByRating(Long addressId, Pageable pageable);
}
