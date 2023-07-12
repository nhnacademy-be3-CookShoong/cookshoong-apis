package store.cookshoong.www.cookshoongbackend.store.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 사업자 : 매장 등록에서 가맹점 등록시 select box 구현을 위하여 사용할 dto.
 *
 * @author seungyeon
 * @since 2023.07.12
 */
@Getter
public class SelectAllMerchantsForUserResponseDto {
    private final Long merchantId;
    private final String merchantName;

    @QueryProjection
    public SelectAllMerchantsForUserResponseDto(Long merchantId, String merchantName){
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }
}
