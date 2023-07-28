package store.cookshoong.www.cookshoongbackend.search.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.config.ElasticSearchConfig;

/**
 * {설명을 작성해주세요}
 *
 * @author koesnam (추만석)
 * @since 2023.07.28
 */
@Import(ElasticSearchConfig.class)
@DataElasticsearchTest
class StoreSearchRepositoryTest {
    @Autowired
    StoreSearchRepository storeSearchRepository;

    @Test
    void setup() {
        storeSearchRepository.searchByKeywordText("", Pageable.ofSize(1));
    }
}
