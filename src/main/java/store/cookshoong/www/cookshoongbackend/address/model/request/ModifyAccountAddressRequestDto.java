package store.cookshoong.www.cookshoongbackend.address.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원이 주문할 때 상세주소를 변경하는 경우 사용되는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@AllArgsConstructor
public class ModifyAccountAddressRequestDto {

    private String detailPlace;
}
