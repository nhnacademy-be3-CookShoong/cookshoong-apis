package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;

/**
 * 한 주문에 대한 리뷰 조회 dto.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Getter
public class SelectReviewResponseDto {
    private final Long storeId;
    private final String storeName;
    @Setter
    private String storeImageName;
    private final String storeImageLocationType;
    private final String storeImageDomainName;
    private final Long reviewId;
    private final String contents;
    private final Integer rating;
    private final LocalDateTime writtenAt;
    private final LocalDateTime updatedAt;
    @Setter
    private Set<SelectReviewImageResponseDto> imageResponses;
    private final Set<SelectReviewOrderMenuResponseDto> menuResponses;
    private final Set<SelectBusinessReviewResponseDto> replyResponses;

    /**
     * Instantiates a new Select review response dto.
     *
     * @param storeId                the store id
     * @param storeName              the store name
     * @param storeImageName         the store image name
     * @param storeImageLocationType the store image location type
     * @param storeImageDomainName   the store image domain name
     * @param reviewId               the review id
     * @param contents               the contents
     * @param rating                 the rating
     * @param imageResponses      the image response dtos
     * @param menuResponses       the menu response dtos
     * @param replyResponses      the reply response dtos
     */
    @QueryProjection
    public SelectReviewResponseDto(Long storeId, String storeName, String storeImageName, String storeImageLocationType,
                                   String storeImageDomainName, Long reviewId, String contents, Integer rating,
                                   LocalDateTime writtenAt, LocalDateTime updatedAt,
                                   Set<SelectReviewImageResponseDto> imageResponses,
                                   Set<SelectReviewOrderMenuResponseDto> menuResponses,
                                   Set<SelectBusinessReviewResponseDto> replyResponses) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeImageName = storeImageName;
        this.storeImageLocationType = storeImageLocationType;
        this.storeImageDomainName = storeImageDomainName;
        this.reviewId = reviewId;
        this.contents = contents;
        this.rating = rating;
        this.writtenAt = writtenAt;
        this.updatedAt = updatedAt;
        this.imageResponses = validSortedSet(imageResponses);
        this.menuResponses = validSortedSet(menuResponses);
        this.replyResponses = validSortedSet(replyResponses);
    }

    private <T extends Remove> SortedSet<T> validSortedSet(Set<T> responses) {
        responses.removeIf(T::isNull);
        return new TreeSet<>(responses);
    }
}
