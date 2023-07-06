package store.cookshoong.www.cookshoongbackend.store.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {설명을 작성해주세요}.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@Getter
@AllArgsConstructor
public class StoreListResponseDto {
    private Long storeId;
    private String loginId;
    private String storeName;
    private String storeMainAddress;
    private String storeDetailAddress;
    private String storeStatus;
}
