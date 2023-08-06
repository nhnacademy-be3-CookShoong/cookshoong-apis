package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

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
    @Pattern(regexp = RegularExpressions.NUMBER_ONLY, message = ValidationFailureMessages.NUMBER_ONLY)
    private String businessLicenseNumber;
    @NotBlank
    @Pattern(regexp = RegularExpressions.LETTER_ONLY, message = ValidationFailureMessages.LETTER_ONLY)
    private String representativeName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;
    @NotBlank
    private String storeName;
    @NotBlank
    @Pattern(regexp = RegularExpressions.MAIN_DETAIL_ADDRESS, message = ValidationFailureMessages.MAIN_DETAIL_ADDRESS)
    private String mainPlace;
    @NotBlank
    @Pattern(regexp = RegularExpressions.MAIN_DETAIL_ADDRESS, message = ValidationFailureMessages.MAIN_DETAIL_ADDRESS)
    private String detailPlace;
    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;
    @NotBlank
    @Pattern(regexp = RegularExpressions.NUMBER_ONLY, message = ValidationFailureMessages.NUMBER_ONLY)
    private String phoneNumber;
    @NotBlank
    private String description;
    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax("5.0")
    private BigDecimal earningRate;
    @NotNull
    @PositiveOrZero
    @Pattern(regexp = RegularExpressions.NUMBER_ONLY, message = ValidationFailureMessages.NUMBER_ONLY)
    private Integer minimumOrderPrice;
    @NotNull
    private List<String> storeCategories;
    @NotBlank
    private String bankCode;
    @NotBlank
    @Pattern(regexp = RegularExpressions.NUMBER_ONLY, message = ValidationFailureMessages.NUMBER_ONLY)
    private String bankAccount;

}
