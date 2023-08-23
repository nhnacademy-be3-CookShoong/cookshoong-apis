package store.cookshoong.www.cookshoongbackend.shop.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사업자 : 매장 상태를 변경할 때 사용되는 dto.
 *
 * @author seungyeon
 * @since 2023.07.16
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreStatusRequestDto {
    @NotBlank
    private String statusCode;
}
