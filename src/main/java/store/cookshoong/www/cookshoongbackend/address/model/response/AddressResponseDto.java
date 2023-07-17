package store.cookshoong.www.cookshoongbackend.address.model.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 주소에 정보를 모두 응답해 주는 Dto.
 * 정보는 메인과 상세주소만 보여주도록 한다.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@ToString
@AllArgsConstructor
public class AddressResponseDto {

    private Long id;
    private String mainPlace;
    private String detailPlace;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
