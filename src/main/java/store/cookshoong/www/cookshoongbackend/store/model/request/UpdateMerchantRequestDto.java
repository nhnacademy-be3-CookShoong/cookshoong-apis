package store.cookshoong.www.cookshoongbackend.store.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가맹점 수정을 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMerchantRequestDto {
    private String merchantName;
}
