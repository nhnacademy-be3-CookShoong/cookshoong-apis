package store.cookshoong.www.cookshoongbackend.search.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 매장 도큐먼트 응답 Dto.
 *
 * @author papel
 * @since 2023.07.21
 */
@Getter
public class StoreDocumentResponseDto {
    private Long id;
    private String name;
    private String description;
    @Setter
    private String savedName;
    private String locationType;
    private String domainName;
    @Setter
    private String storeStatus;
    private Integer minimumOrderPrice;
    private Integer minimumCookingTime;
    private Integer countReview;
    private Float avgRating;
    @Setter
    private List<String> categories;
    @Setter
    private boolean offerCoupon;


    public StoreDocumentResponseDto(Long id, String name, String description, String savedName,
                                    String locationType, String domainName, String storeStatus,
                                    Integer minimumOrderPrice, Integer minimumCookingTime, Integer countReview, Float avgRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.savedName = savedName;
        this.locationType = locationType;
        this.domainName = domainName;
        this.storeStatus = storeStatus;
        this.minimumOrderPrice = minimumOrderPrice;
        this.minimumCookingTime = minimumCookingTime;
        this.countReview = countReview;
        this.avgRating = avgRating;
    }

    public static StoreDocumentResponseDto from(StoreDocument storeDocument) {
        return new StoreDocumentResponseDto(
            storeDocument.getId(),
            storeDocument.getName(),
            storeDocument.getDescription(),
            storeDocument.getSavedName(),
            storeDocument.getLocationType(),
            storeDocument.getDomainName(),
            storeDocument.getStoreStatus(),
            storeDocument.getMinimumOrderPrice(),
            storeDocument.getMinimumCookingTime(),
            storeDocument.getCountReview(),
            storeDocument.getAvgRating());
    }
}
