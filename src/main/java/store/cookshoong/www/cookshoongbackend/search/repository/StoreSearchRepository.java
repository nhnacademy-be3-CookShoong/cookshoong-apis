package store.cookshoong.www.cookshoongbackend.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.cookshoong.www.cookshoongbackend.search.model.StoreDocument;

/**
 * 매장 도큐먼트 레포지토리.
 *
 * @author papel
 * @since 2023.07.21
 */
public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, Long>, StoreSearchRepositoryCustom {
}
