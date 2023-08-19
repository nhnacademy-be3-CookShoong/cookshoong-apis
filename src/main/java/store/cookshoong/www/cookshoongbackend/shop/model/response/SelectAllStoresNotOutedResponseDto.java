package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 위치에서 3km 이내에 있는 매장을 보여줄 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.14
 */
@Getter
public class SelectAllStoresNotOutedResponseDto {
    private final Long id;
    private final String name;
    private final String storeStatus;
    private final String mainPlace;
    private final String detailPlace;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String category;
    @Setter
    private String savedName;
    private String locationType;
    private String domainName;
    private Integer minimumOrderPrice;
    private Integer deliveryCost;

    /**
     * 회원 위치에서 3km 이내에 있는 매장을 보여줄 QueryDsl 로 사용될 메서드.
     *
     * @param id                매장 아이디
     * @param name              매장 이름
     * @param storeStatus        매장의 상태
     * @param mainPlace         매장의 메인주소
     * @param detailPlace       매장의 상세주소
     * @param latitude          매장의 상세주소
     * @param longitude         매장의 상세주소
     */
    @QueryProjection
    public SelectAllStoresNotOutedResponseDto(Long id, String name, String storeStatus, String mainPlace,
                                              String detailPlace, BigDecimal latitude, BigDecimal longitude, String category,
                                              String savedName, String locationType, String domainName, Integer minimumOrderPrice, Integer deliveryCost) {
        this.id = id;
        this.name = name;
        this.storeStatus = storeStatus;
        this.mainPlace = mainPlace;
        this.detailPlace = detailPlace;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.savedName = savedName;
        this.locationType = locationType;
        this.domainName = domainName;
        this.minimumOrderPrice = minimumOrderPrice;
        this.deliveryCost = deliveryCost;
    }
}
