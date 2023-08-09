package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 영업점 정보에 대한 수정 dto.
 *
 * @author seungyeon
 * @since 2023.08.08
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStoreInfoRequestDto {
    private LocalDate openingDate;
    private String storeName;
    private String mainPlace;
    private String detailPlace;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phoneNumber;
    private String description;
    private BigDecimal earningRate;
    private Integer minimumOrderPrice;
    private Integer deliveryCost;
}
