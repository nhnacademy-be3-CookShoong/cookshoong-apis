package store.cookshoong.www.cookshoongbackend.account.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 폼에서 회원가입 요청을 위해 보내는 Dto.
 *
 * @author koesnam
 * @since 2023/07/04
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuth2SignUpRequestDto {
    @Valid
    @NotNull
    private SignUpRequestDto signUpRequestDto;
    @NotBlank
    private String accountCode;
    @NotBlank
    private String provider;
}
