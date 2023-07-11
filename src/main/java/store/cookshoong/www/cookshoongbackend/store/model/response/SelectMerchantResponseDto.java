package store.cookshoong.www.cookshoongbackend.store.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 가맹점 조회를 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
public class SelectMerchantResponseDto {
    private final Long merchantId;
    private final String merchantName;

    /**
     * 가맹점 리스트 조회 dto.
     *
     * @param merchantName 가맹점 이름
     */
    @QueryProjection
    public SelectMerchantResponseDto(Long merchantId, String merchantName) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }
}
