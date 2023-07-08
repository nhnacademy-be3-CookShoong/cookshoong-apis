package store.cookshoong.www.cookshoongbackend.store.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 매장 리스트 응답을 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@Getter
public class SelectAllStoresResponseDto {
    private final Long storeId;
    private final String loginId;
    private final String storeName;
    private final String storeMainAddress;
    private final String storeDetailAddress;
    private final String storeStatus;

    /**
     * 사업자 회원 : 사업자 회원이 소유한 매장 리스트.
     *
     * @param storeId            매장 아이디
     * @param loginId            사업자 회원의 로그인 아이디
     * @param storeName          매장 이름
     * @param storeMainAddress   매장의 메인주소
     * @param storeDetailAddress 매장의 상세주소
     * @param storeStatus        매장 상태
     */
    @QueryProjection
    public SelectAllStoresResponseDto(Long storeId, String loginId, String storeName, String storeMainAddress,
                                      String storeDetailAddress, String storeStatus) {
        this.storeId = storeId;
        this.loginId = loginId;
        this.storeName = storeName;
        this.storeMainAddress = storeMainAddress;
        this.storeDetailAddress = storeDetailAddress;
        this.storeStatus = storeStatus;
    }
}
