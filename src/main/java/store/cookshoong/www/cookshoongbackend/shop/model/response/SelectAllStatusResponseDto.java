package store.cookshoong.www.cookshoongbackend.shop.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사업자 : 매장 상태 dto.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.17
 */
@AllArgsConstructor
@Getter
public class SelectAllStatusResponseDto {
    private String storeStatusCode;
    private String description;
}
