package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.file.Image;

/**
 * 매장 수정을 위한 dto.
 *
 * @author seungyeon
 * @since 2023.07.13
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreRequestDto {
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
    private String description;
    private BigDecimal earningRate;
    private Image image;
    private String bankName;
    private String bankAccount;
}
