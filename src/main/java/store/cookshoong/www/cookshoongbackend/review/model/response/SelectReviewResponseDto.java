package store.cookshoong.www.cookshoongbackend.review.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;
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
    private final String contents;
    private final Integer rating;
    private final LocalDateTime writtenAt;
    private final LocalDateTime updatedAt;
    @Setter
    private List<SelectReviewImageResponseDto> imageResponseDtos;
    private final List<SelectReviewOrderMenuResponseDto> menuResponseDtos;
    private final List<SelectBusinessReviewResponseDto> replyResponseDtos;

    /**
     * Instantiates a new Select review response dto.
     *
     * @param storeId                the store id
     * @param storeName              the store name
     * @param storeImageName         the store image name
     * @param storeImageLocationType the store image location type
     * @param storeImageDomainName   the store image domain name
     * @param contents               the contents
     * @param rating                 the rating
     * @param imageResponseDtos      the image response dtos
     * @param menuResponseDtos       the menu response dtos
     * @param replyResponseDtos      the reply response dtos
     */
    @QueryProjection
    public SelectReviewResponseDto(Long storeId, String storeName, String storeImageName, String storeImageLocationType,
                                   String storeImageDomainName, String contents, Integer rating, LocalDateTime writtenAt, LocalDateTime updatedAt,
                                   List<SelectReviewImageResponseDto> imageResponseDtos, List<SelectReviewOrderMenuResponseDto> menuResponseDtos,
                                   List<SelectBusinessReviewResponseDto> replyResponseDtos) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeImageName = storeImageName;
        this.storeImageLocationType = storeImageLocationType;
        this.storeImageDomainName = storeImageDomainName;
        this.contents = contents;
        this.rating = rating;
        this.writtenAt = writtenAt;
        this.updatedAt = updatedAt;
        this.imageResponseDtos = imageResponseDtos;
        this.menuResponseDtos = menuResponseDtos;
        this.replyResponseDtos = replyResponseDtos;
    }
}
