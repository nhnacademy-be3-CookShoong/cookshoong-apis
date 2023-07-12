package store.cookshoong.www.cookshoongbackend.store.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    //TODO 2. validation 재작성
    private String merchantName;
    @NotBlank
    private String businessLicenseNumber;
    private String representativeName;
    private LocalDate openingDate;
    private String storeName;
    private String mainPlace;
    private String detailPlace;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phoneNumber;
    private String businessLicense;
    private String description;
    private BigDecimal earningRate;
    private String bankName;
    private String bankAccount;
}
