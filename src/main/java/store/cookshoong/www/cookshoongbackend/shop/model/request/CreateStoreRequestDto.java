package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 매장 등록 Dto.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStoreRequestDto {
    private String merchantName;
    @NotBlank
    private String businessLicenseNumber;
    @NotBlank
    private String representativeName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;
    @NotBlank
    private String storeName;
    @NotBlank
    private String mainPlace;
    private String detailPlace;
    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String businessLicense;
    private String description;
    @NotNull
    private BigDecimal earningRate;
    private String image;
    @NotNull
    private List<String> storeCategories;
    @NotBlank
    private String bankName;
    @NotBlank
    private String bankAccount;
}
