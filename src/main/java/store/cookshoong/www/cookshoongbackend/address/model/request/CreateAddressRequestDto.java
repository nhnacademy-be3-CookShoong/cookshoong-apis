package store.cookshoong.www.cookshoongbackend.address.model.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 매장에서 주소를 등록할 때 사용되는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
public class CreateAddressRequestDto {

    private String mainPlace;

    private String detailPlace;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
