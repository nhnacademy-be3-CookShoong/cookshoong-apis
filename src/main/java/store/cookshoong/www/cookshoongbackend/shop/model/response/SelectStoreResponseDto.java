package store.cookshoong.www.cookshoongbackend.shop.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사업자 : 기존 SelectStoreResponseVo 에서는 image 저장된 이름을 가져온다.
 * front에 보내줄 때는 경로가 포함된 이름이 필요하다.
 *
 * @author seungyeon
 * @since 2023.07.23
 */
@Getter
@RequiredArgsConstructor
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
    private final String pathName;

    /**
     * 사업자 : 해당 매장 정보에대한 response dto.
     *
     * @param vo       데이터베이스에서 해당 매장 관련 정보를 가져옴
     * @param pathName 매장 사진 경로 + 저장된 이름 + 형식
     */
    public SelectStoreResponseDto(SelectStoreResponseVo vo, String pathName) {
        this.businessLicenseNumber = vo.getBusinessLicenseNumber();
        this.representativeName = vo.getRepresentativeName();
        this.openingDate = vo.getOpeningDate();
        this.storeName = vo.getStoreName();
        this.phoneNumber = vo.getPhoneNumber();
        this.mainPlace = vo.getMainPlace();
        this.detailPlace = vo.getDetailPlace();
        this.latitude = vo.getLatitude();
        this.longitude = vo.getLongitude();
        this.defaultEarningRate = vo.getDefaultEarningRate();
        this.description = vo.getDescription();
        this.bankCode = vo.getBankCode();
        this.bankAccountNumber = vo.getBankAccountNumber();
        this.pathName = pathName;
    }
}
