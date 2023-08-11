package store.cookshoong.www.cookshoongbackend.account.model.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

/**
 * 회원 정보를 수정하는 요청 Dto.
 *
 * @author koesnam (추만석)
 * @since 2023.07.08
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateAccountInfoRequestDto {
    @NotBlank
    @Length(min = 1, max = 30)
    @Pattern(regexp = RegularExpressions.LETTER_WITH_NUMBER, message = ValidationFailureMessages.LETTER_WITH_NUMBER)
    private String nickname;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 11, max = 11)
    @Pattern(regexp = RegularExpressions.NUMBER_ONLY, message = ValidationFailureMessages.NUMBER_ONLY)
    private String phoneNumber;
}
