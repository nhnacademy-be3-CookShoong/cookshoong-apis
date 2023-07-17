package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 사업자 : 매장 상태 변경에 radio button 구현에 사용되는 status dto.
 *
 * @author seungyeon
 * @since 2023.07.16
 */
@Getter
public class SelectAllStatusResponseDto {
    private final String statusCode;
    private final String statusName;

    @QueryProjection
    public SelectAllStatusResponseDto(String statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

}
