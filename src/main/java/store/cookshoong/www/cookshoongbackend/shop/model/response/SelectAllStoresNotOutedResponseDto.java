package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.ToString;

/**
 * 회원 위치에서 3km 이내에 있는 매장을 보여줄 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.14
 */
@Getter
@ToString
public class SelectAllStoresNotOutedResponseDto {
    private final Long id;
    private final String name;
    private final String storeStatus;
    private final String mainPlace;
    private final String detailPlace;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String category;

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
                                              String detailPlace, BigDecimal latitude, BigDecimal longitude, String category) {
        this.id = id;
        this.name = name;
        this.storeStatus = storeStatus;
        this.mainPlace = mainPlace;
        this.detailPlace = detailPlace;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }
}
