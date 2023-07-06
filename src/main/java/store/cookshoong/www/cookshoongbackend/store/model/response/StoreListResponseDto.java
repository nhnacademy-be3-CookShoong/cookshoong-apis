package store.cookshoong.www.cookshoongbackend.store.model.response;

import lombok.Getter;

/**
 * 매장 리스트 응답을 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@Getter
public class StoreListResponseDto {
    private Long storeId;
    private String loginId;
    private String storeName;
    private String storeMainAddress;
    private String storeDetailAddress;
    private String storeStatus;
}
