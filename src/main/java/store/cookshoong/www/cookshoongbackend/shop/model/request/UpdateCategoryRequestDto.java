package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카테고리 수정을 위한 dto.
 *
 * @author seungyeon
 * @since 2023.07.13
 */
@Getter
@NoArgsConstructor
public class UpdateCategoryRequestDto {
    private List<String> storeCategories;
}
