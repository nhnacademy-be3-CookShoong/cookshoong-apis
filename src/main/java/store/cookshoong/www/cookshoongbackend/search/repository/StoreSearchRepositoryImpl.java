package store.cookshoong.www.cookshoongbackend.search.repository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocument;

/**
 * 매장 도큐먼트 레포지토리 커스텀 구현.
 *
 * @author papel
 * @since 2023.07.21
 */
@RequiredArgsConstructor
@Repository
public class StoreSearchRepositoryImpl implements StoreSearchRepositoryCustom {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<StoreDocument> searchByKeywordText(String keywordText, Pageable pageable) {
        Criteria criteria = Criteria.where("keywordText").matches(keywordText);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(query, StoreDocument.class);

        List<StoreDocument> storeDocuments = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }
}
