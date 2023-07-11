package store.cookshoong.www.cookshoongbackend.store.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 은행 추가 dto.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateBankRequestDto {
    @NotBlank
    private String bankCode;

    @NotBlank
    @Size(min = 1, max = 30)
    private String bankName;
}
