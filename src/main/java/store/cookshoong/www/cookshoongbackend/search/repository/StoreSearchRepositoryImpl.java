package store.cookshoong.www.cookshoongbackend.search.repository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;
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
    private final AddressService addressService;

    @Override
    public Page<StoreDocument> searchByKeywordText(String keywordText, Long addressId, Pageable pageable) {

        MatchQueryBuilder matchQueryBuilder = QueryBuilders
            .matchQuery("keywordText", keywordText)
            .fuzziness("AUTO");

        Double lat = addressService.selectAccountAddressRenewalAt(addressId).getLatitude().doubleValue();
        Double lon = addressService.selectAccountAddressRenewalAt(addressId).getLongitude().doubleValue();

        GeoDistanceQueryBuilder geoDistanceQueryBuilder = QueryBuilders
            .geoDistanceQuery("location")
            .point(lat, lon)
            .distance(3, DistanceUnit.KILOMETERS);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders
            .boolQuery()
            .must(matchQueryBuilder)
            .filter(geoDistanceQueryBuilder);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder)
            .withMinScore(0.6f)
            .build();

        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<StoreDocument> searchByDistance(Long addressId, Pageable pageable) {
        Double lat = addressService.selectAccountAddressRenewalAt(addressId).getLatitude().doubleValue();
        Double lon = addressService.selectAccountAddressRenewalAt(addressId).getLongitude().doubleValue();

        GeoDistanceQueryBuilder geoDistanceQueryBuilder = QueryBuilders
            .geoDistanceQuery("location")
            .point(lat, lon)
            .distance(3, DistanceUnit.KILOMETERS);

        NativeSearchQuery searchQuery = new NativeSearchQuery(geoDistanceQueryBuilder);

        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }
}
