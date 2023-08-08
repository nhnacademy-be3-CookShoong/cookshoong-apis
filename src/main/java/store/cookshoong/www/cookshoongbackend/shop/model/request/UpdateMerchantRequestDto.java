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
 * 가맹점 수정을 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMerchantRequestDto {
    @NotBlank
    @Size(min = 1, max = 20, message = "1~20자 이내로 입력해주세요")
    @Pattern(regexp = RegularExpressions.LETTER_WITH_NUMBER_AND_BLANK, message = ValidationFailureMessages.LETTER_WITH_NUMBER)
    private String merchantName;
}
