package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 등록 Dto.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStoreRequestDto {
    private Long merchantId;
    @NotBlank
    private String businessLicenseNumber;
    @NotBlank
    private String representativeName;
    @NotBlank
    private String openingDate;
    @NotBlank
    private String storeName;
    @NotBlank
    private String mainPlace;
    @NotBlank
    private String detailPlace;
    @NotBlank
    private String latitude;
    @NotBlank
    private String longitude;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String businessLicense;
    @NotBlank
    private String description;
    @NotBlank
    private String earningRate;
    private List<String> storeCategories;
    @NotBlank
    private String bankCode;
    @NotBlank
    private String bankAccount;

}
