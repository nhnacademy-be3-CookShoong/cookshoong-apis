package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 사업자 : 매장 등록에서 은행 등록 시 select box 구현을 위하여 사용할 dto.
 *
 * @author seungyeon
 * @since 2023.07.12
 */
@Getter
public class SelectAllBanksForUserResponseDto {
    private final String bankCode;
    private final String bankName;

    /**
     * 은행명 response dto.
     *
     * @param bankName 은행 이름
     */
    @QueryProjection
    public SelectAllBanksForUserResponseDto(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
    }
}
