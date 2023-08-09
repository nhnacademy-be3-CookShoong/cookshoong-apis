package store.cookshoong.www.cookshoongbackend.search.model;

import lombok.Getter;

/**
 * 매장 도큐먼트 생성 Dto.
 *
 * @author papel
 * @since 2023.07.21
 */
@Getter
public class StoreDocumentRequestDto {
    private Long id;
    private String name;
    private String description;
    private String savedName;
    private String locationType;
    private String domainName;
    private String storeStatus;
    private Integer minimumOrderPrice;
    private Integer minimumCookingTime;
}
