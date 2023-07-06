package store.cookshoong.www.cookshoongbackend.account.model.request;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

/**
 * 회원가입 요청을 받는 Dto.
 *
 * @author koesnam
 * @since 2023.07.05
 */
@Getter
@ToString
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank
    @Length(min = 1, max = 30)
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    @Length(min = 2, max = 30)
    @Pattern(regexp = RegularExpressions.LETTER_ONLY, message = ValidationFailureMessages.LETTER_ONLY)
    private String name;
    @NotBlank
    @Length(min = 1, max = 30)
    @Pattern(regexp = RegularExpressions.LETTER_WITH_NUMBER, message = ValidationFailureMessages.LETTER_WITH_NUMBER)
    private String nickname;
    @NotBlank
    @Email
    private String email;
    @Past
    @NotNull
    private LocalDate birthday;
    @NotBlank
    @Pattern(regexp = RegularExpressions.NUMBER_ONLY, message = ValidationFailureMessages.NUMBER_ONLY)
    private String phoneNumber;
}
