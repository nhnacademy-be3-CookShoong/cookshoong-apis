package store.cookshoong.www.cookshoongbackend.shop.model.request;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가맹점 등록과 수정 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateMerchantRequestDto {
    @NotBlank
    @Size(min = 1, max = 20, message = "1~20자 이내로 입력해주세요")
    private String merchantName;
}
