package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
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
    private final String loginId;
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

    /**
     * 사업자 회원 : 사업자 회원이 본인 매장 정보 조회.
     *
     * @param loginId               로그인 아이디
     * @param businessLicenseNumber 사압자등록번호
     * @param representativeName    대표자 이름
     * @param openingDate           개업일자
     * @param storeName             상호명
     * @param phoneNumber           가게번호
     * @param mainPlace             메인주소
     * @param detailPlace           상세주소
     * @param latitude              위도
     * @param longitude             경도
     * @param defaultEarningRate    기본 적립률
     * @param description           매장 설명
     * @param bankCode              정산 은행 이름
     * @param bankAccountNumber     정산받을 계좌번호
     */
    @QueryProjection
    public SelectStoreResponseDto(String loginId, String businessLicenseNumber, String representativeName,
                                  LocalDate openingDate, String storeName, String phoneNumber, String mainPlace,
                                  String detailPlace, BigDecimal latitude, BigDecimal longitude, BigDecimal defaultEarningRate, String description, String bankCode,
                                  String bankAccountNumber) {
        this.loginId = loginId;
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
    }
}
