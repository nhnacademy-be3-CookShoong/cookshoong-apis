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
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.Image;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;
    @NotBlank
    private String storeName;
    @NotBlank
    private String mainPlace;
    @NotBlank
    private String detailPlace;
    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private Image businessLicense;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal earningRate;
    private Image image;
    @NotNull
    private List<String> storeCategories;
    @NotBlank
    private String bankCode;
    @NotBlank
    private String bankAccount;

}
