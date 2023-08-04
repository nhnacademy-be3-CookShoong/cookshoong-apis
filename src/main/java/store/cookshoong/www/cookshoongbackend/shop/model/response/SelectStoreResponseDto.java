package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * 사업자 : 기존 SelectStoreResponseVo 에서는 image 저장된 이름을 가져온다.
 * front에 보내줄 때는 경로가 포함된 이름이 필요하다.
 *
 * @author seungyeon
 * @since 2023.07.23
 */
@Getter
public class SelectStoreResponseDto {
    private final String businessLicenseNumber;
    private final String representativeName;
    private final LocalDate openingDate;
    private final String storeName;
    private final String phoneNumber;
    private final String mainPlace;
    private final String detailPlace;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final BigDecimal defaultEarningRate;
    private final String description;
    private final String bankCode;
    private final String bankAccountNumber;
    @Setter
    private String pathName;

    /**
     * 사업자 : 해당 매장 정보에대한 response dto.
     *
     * @param businessLicenseNumber the business license number
     * @param representativeName    the representative name
     * @param openingDate           the opening date
     * @param storeName             the store name
     * @param phoneNumber           the phone number
     * @param mainPlace             the main place
     * @param detailPlace           the detail place
     * @param latitude              the latitude
     * @param longitude             the longitude
     * @param defaultEarningRate    the default earning rate
     * @param description           the description
     * @param bankCode              the bank code
     * @param bankAccountNumber     the bank account number
     * @param pathName              매장 사진 경로 + 저장된 이름 + 형식
     */
    @QueryProjection
    public SelectStoreResponseDto(String businessLicenseNumber, String representativeName,
                                  LocalDate openingDate, String storeName, String phoneNumber, String mainPlace,
                                  String detailPlace, BigDecimal latitude, BigDecimal longitude, BigDecimal defaultEarningRate, String description, String bankCode,
                                  String bankAccountNumber, String pathName) {
        this.businessLicenseNumber = businessLicenseNumber;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.mainPlace = mainPlace;
        this.detailPlace = detailPlace;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultEarningRate = defaultEarningRate;
        this.description = description;
        this.bankCode = bankCode;
        this.bankAccountNumber = bankAccountNumber;
        this.pathName = pathName;
    }
}
