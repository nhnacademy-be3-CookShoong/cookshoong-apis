package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 모든 은행리스트를 조회할때 사용할 response dto.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Getter
public class SelectAllBanksResponseDto {
    private final String bankCode;
    private final String bankName;

    /**
     * 은행명 response dto.
     *
     * @param bankName 은행 이름
     */
    @QueryProjection
    public SelectAllBanksResponseDto(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
    }
}
