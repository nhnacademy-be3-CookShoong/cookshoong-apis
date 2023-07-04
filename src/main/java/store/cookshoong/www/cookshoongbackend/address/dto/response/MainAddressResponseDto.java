package store.cookshoong.www.cookshoongbackend.address.dto.response;

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
public class MainAddressResponseDto {

    private String mainAddress;
}
