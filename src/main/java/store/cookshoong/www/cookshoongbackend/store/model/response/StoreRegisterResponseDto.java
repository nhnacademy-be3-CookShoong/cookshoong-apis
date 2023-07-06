package store.cookshoong.www.cookshoongbackend.store.model.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 매장 등록을 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
@AllArgsConstructor
public class StoreRegisterResponseDto {
    private String merchantName;
    private String businessLicenseNumber;
    private String representativeName;
    private LocalDate openingDate;
    private String storeName;
    private String phoneNumber;
    private String storeAddress;
    private String businessLicense;
    private String description;
}
