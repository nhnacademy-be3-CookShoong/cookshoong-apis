package store.cookshoong.www.cookshoongbackend.search.model;

import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * 엘라스틱 서치 매장 도큐먼트 엔티티.
 *
 * @author papel
 * @since 2023.07.21
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "store")
public class StoreDocument {
    @Id
    private Long id;
    @Field("name")
    private String name;
    @Field("description")
    private String description;
    @Field("saved_name")
    private String savedName;
    @Field("location_code")
    private String locationType;
    @Field("domain_name")
    private String domainName;
    @Field("store_status_code")
    private String storeStatus;
    @Field("minimum_order_price")
    private Integer minimumOrderPrice;
    @Field("minimum_cooking_time")
    private Integer minimumCookingTime;
    @Field("count_review")
    private Integer countReview;
    @Field("count_rating")
    private Float avgRating;

    public static StoreDocument from(StoreDocumentRequestDto storeDocumentRequestDto) {
        return StoreDocument.builder()
            .id(storeDocumentRequestDto.getId())
            .name(storeDocumentRequestDto.getName())
            .description(storeDocumentRequestDto.getDescription())
            .savedName(storeDocumentRequestDto.getSavedName())
            .locationType(storeDocumentRequestDto.getLocationType())
            .domainName(storeDocumentRequestDto.getDomainName())
            .storeStatus(storeDocumentRequestDto.getStoreStatus())
            .minimumOrderPrice(storeDocumentRequestDto.getMinimumOrderPrice())
            .minimumCookingTime(storeDocumentRequestDto.getMinimumCookingTime())
            .countReview(storeDocumentRequestDto.getCountReview())
            .avgRating(storeDocumentRequestDto.getAvgRating())
            .build();
    }
}
