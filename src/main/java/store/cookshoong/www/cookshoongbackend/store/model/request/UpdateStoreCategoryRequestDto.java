package store.cookshoong.www.cookshoongbackend.store.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 카테고리 수정을 위한 dto.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreCategoryRequestDto {
    private String storeCategoryName;
}
