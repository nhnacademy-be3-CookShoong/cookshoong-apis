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
    private final String bankTypeCode;
    private final String description;

    /**
     * 은행명 response dto.
     *
     * @param bankTypeCode the bank type code
     * @param description  the description
     */
    @QueryProjection
    public SelectAllBanksResponseDto(String bankTypeCode, String description) {
        this.bankTypeCode = bankTypeCode;
        this.description = description;
    }
}
