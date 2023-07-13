package store.cookshoong.www.cookshoongbackend.shop.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 카테고리 등록을 위한 dto.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStoreCategoryRequestDto {
    @NotBlank
    @Size(min = 1, max = 10)
    private String storeCategoryCode;
    @NotBlank
    @Size(min = 1, max = 10)
    private String storeCategoryName;
}
