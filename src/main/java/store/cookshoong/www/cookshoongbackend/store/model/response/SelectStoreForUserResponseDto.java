package store.cookshoong.www.cookshoongbackend.store.model.response;

import java.time.LocalDate;
import lombok.Getter;

/**
 * 일반 고객이 볼 수 있는 매장 정보 조회.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
@Getter
public class SelectStoreForUserResponseDto {
    private String businessLicenseNumber;
    private String representativeName;
    private LocalDate openingDate;
    private String storeName;
    private String phoneNumber;
    private String mainPlace;
    private String detailPlace;
    private String description;
}
