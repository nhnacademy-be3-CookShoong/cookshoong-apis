package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.List;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableStoreCouponPolicyResponseDto;
import lombok.Setter;
import store.cookshoong.www.cookshoongbackend.coupon.model.response.SelectProvableStoreCouponPolicyResponseDto;
import lombok.Setter;

/**
 * 일반 고객이 볼 수 있는 매장 정보 조회.
 *
 * @author seungyeon
 * @since 2023.07.07
 */
@Getter
public class SelectStoreForUserResponseDto {
    private final String businessLicenseNumber;
    private final String representativeName;
    private final LocalDate openingDate;
    private final String storeName;
    private final String phoneNumber;
    private final String mainPlace;
    private final String detailPlace;
    private final String description;
    private final Integer minimumOrderPrice;
    private final Integer deliveryCost;
    private final String locationType;
    private final String domainName;
    @Setter
    private String savedName;
    @Setter
    private List<SelectProvableStoreCouponPolicyResponseDto> provableCouponPolicies;

    /**
     * 일반회원 : 일반회원이 조회할 수 있는 매장의 정보.
     *
     * @param businessLicenseNumber 사업자 번호
     * @param representativeName    대표 이름
     * @param openingDate           개업일
     * @param storeName             가게 이름
     * @param phoneNumber           핸드폰 번호
     * @param mainPlace             메인주소
     * @param detailPlace           상세주소
     * @param description           매장 설명
     */
    @QueryProjection
    public SelectStoreForUserResponseDto(String businessLicenseNumber, String representativeName, LocalDate openingDate,
                                         String storeName, String phoneNumber, String mainPlace, String detailPlace, String description,
                                         String locationType, String domainName, String savedName, Integer minimumOrderPrice, Integer deliveryCost) {
        this.businessLicenseNumber = businessLicenseNumber;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
        this.mainPlace = mainPlace;
        this.detailPlace = detailPlace;
        this.description = description;
        this.locationType = locationType;
        this.domainName = domainName;
        this.savedName = savedName;
        this.minimumOrderPrice = minimumOrderPrice;
        this.deliveryCost = deliveryCost;
    }
}
