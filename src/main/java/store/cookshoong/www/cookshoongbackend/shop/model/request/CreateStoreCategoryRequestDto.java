package store.cookshoong.www.cookshoongbackend.shop.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

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
    @Pattern(regexp = RegularExpressions.ENGLISH_UPPER_ONLY, message = ValidationFailureMessages.ENGLISH_UPPER_ONLY)
    private String storeCategoryCode;
    @NotBlank
    @Size(min = 1, max = 10)
    @Pattern(regexp = RegularExpressions.LETTER_ONLY_WITH_BLANK, message = ValidationFailureMessages.LETTER_ONLY)
    private String storeCategoryName;
}
