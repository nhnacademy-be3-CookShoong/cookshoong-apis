package store.cookshoong.www.cookshoongbackend.address.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상세 주소를 응답해 주는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
public class AccountAddressResponseDto {

    private Long id;
    private String alias;
    private String mainPlace;
    private String detailPlace;
}
