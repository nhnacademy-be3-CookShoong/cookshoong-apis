package store.cookshoong.www.cookshoongbackend.store.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

/**
 * 사업자입장에서 매장 조회를 위한 Dto.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Getter
public class SelectStoreResponseDto {
    private String loginId;
    private String businessLicenseNumber;
    private String representativeName;
    private LocalDate openingDate;
    private String storeName;
    private String phoneNumber;
    private String mainPlace;
    private String detailPlace;
    private BigDecimal defaultEarningRate;
    private String description;
    private String bankName;
    private String bankAccountNumber;
}
