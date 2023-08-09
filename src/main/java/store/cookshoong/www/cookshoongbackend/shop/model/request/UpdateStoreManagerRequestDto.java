package store.cookshoong.www.cookshoongbackend.shop.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 사업자 정보에 대한 수정 dto.
 *
 * @author seungyeon
 * @since 2023.07.13
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreManagerRequestDto {
    private String businessLicenseNumber;
    private String representativeName;
    private String bankCode;
    private String bankAccount;
}
