package store.cookshoong.www.cookshoongbackend.search.repository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
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
    private static final String STORE_STATUS_CODE = "store_status_code";
    private static final String OUTED = "OUTED";
    private static final String LOCATION = "location";

    private GeoDistanceQueryBuilder buildGeoDistanceQuery(Long addressId) {
        double lat = addressService.selectAccountChoiceAddress(addressId).getLatitude().doubleValue();
        double lon = addressService.selectAccountChoiceAddress(addressId).getLongitude().doubleValue();

        return QueryBuilders
            .geoDistanceQuery(LOCATION)
            .point(lat, lon)
            .distance(3, DistanceUnit.KILOMETERS);
    }

    private GeoDistanceSortBuilder buildGeoDistanceSort(Long addressId) {
        double lat = addressService.selectAccountChoiceAddress(addressId).getLatitude().doubleValue();
        double lon = addressService.selectAccountChoiceAddress(addressId).getLongitude().doubleValue();

        return SortBuilders.geoDistanceSort(LOCATION, lat, lon)
            .order(SortOrder.ASC)
            .unit(DistanceUnit.KILOMETERS);
    }

    @Override
    public Page<StoreDocument> searchByDistance(Long addressId, Pageable pageable) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders
            .boolQuery()
            .mustNot(QueryBuilders.termQuery(STORE_STATUS_CODE, OUTED))
            .must(buildGeoDistanceQuery(addressId));

        FieldSortBuilder sortBuilder = SortBuilders.fieldSort(STORE_STATUS_CODE)
            .order(SortOrder.DESC);

        FieldSortBuilder sortBuilderById = SortBuilders.fieldSort("store_id")
            .order(SortOrder.ASC);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder)
            .withPageable(pageable)
            .withSorts(sortBuilder, sortBuilderById, buildGeoDistanceSort(addressId))
            .build();

        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<StoreDocument> searchByRating(Long addressId, Pageable pageable) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders
            .boolQuery()
            .mustNot(QueryBuilders.termQuery(STORE_STATUS_CODE, OUTED))
            .must(buildGeoDistanceQuery(addressId));

        FieldSortBuilder sortBuilderById = SortBuilders.fieldSort("count_rating")
            .order(SortOrder.ASC);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder)
            .withPageable(pageable)
            .withSorts(sortBuilderById, buildGeoDistanceSort(addressId))
            .build();

        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<StoreDocument> searchByKeywordText(String keywordText, Long addressId, Pageable pageable) {

        MatchQueryBuilder matchQueryBuilderNori = QueryBuilders
            .matchQuery("keywordText_nori", keywordText)
            .fuzziness("AUTO");

        MatchQueryBuilder matchQueryBuilderJamoName = QueryBuilders
            .matchQuery("keywordText_jaso_name", keywordText);

        MatchQueryBuilder matchQueryBuilderJamoPlace = QueryBuilders
            .matchQuery("keywordText_jaso_place", keywordText);

        MatchQueryBuilder matchQueryBuilderJamoCategories = QueryBuilders
            .matchQuery("keywordText_jaso_categories", keywordText);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders
            .boolQuery()
            .mustNot(QueryBuilders.termQuery(STORE_STATUS_CODE, OUTED))
            .should(matchQueryBuilderNori)
            .should(matchQueryBuilderJamoName)
            .should(matchQueryBuilderJamoPlace)
            .should(matchQueryBuilderJamoCategories)
            .filter(buildGeoDistanceQuery(addressId))
            .minimumShouldMatch(1);

        FieldSortBuilder sortBuilder = SortBuilders.fieldSort(STORE_STATUS_CODE)
            .order(SortOrder.DESC);

        FieldSortBuilder sortBuilderById = SortBuilders.fieldSort("store_id")
            .order(SortOrder.ASC);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder)
            .withPageable(pageable)
            .withSorts(sortBuilder, sortBuilderById, buildGeoDistanceSort(addressId))
            .build();

        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }

}
