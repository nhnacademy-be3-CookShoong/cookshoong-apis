package store.cookshoong.www.cookshoongbackend.store.model.response;

import lombok.Getter;

/**
 * 가맹점 조회를 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
public class SelectMerchantResponseDto {
    private String merchantName;
}
