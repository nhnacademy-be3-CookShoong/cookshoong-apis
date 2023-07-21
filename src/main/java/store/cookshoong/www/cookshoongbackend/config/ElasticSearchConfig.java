package store.cookshoong.www.cookshoongbackend.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import store.cookshoong.www.cookshoongbackend.search.repository.StoreSearchRepository;

/**
 * 엘라스틱 서치 설정.
 *
 * @author papel
 * @since 2023.07.21
 */
@Configuration
@EnableElasticsearchRepositories(basePackageClasses = {StoreSearchRepository.class})
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {


    /**
     * 엘라스틱 서치 클라이언트의 주소를 설정.
     */
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo("180.210.80.12:9200")
            .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
