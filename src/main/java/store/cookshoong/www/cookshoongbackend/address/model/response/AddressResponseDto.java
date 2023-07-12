package store.cookshoong.www.cookshoongbackend.address.model.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 주소에 정보를 모두 응답해 주는 Dto.
 * 정보는 메인과 상세주소만 보여주도록 한다.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
public class AddressResponseDto {

    private String mainAddress;
    private String detailAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
