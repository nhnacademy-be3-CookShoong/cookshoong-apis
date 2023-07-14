package store.cookshoong.www.cookshoongbackend.address.model.request;

import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

/**
 * 회원이 주문할 때 상세주소를 변경하는 경우 사용되는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyAccountAddressRequestDto {

    @Length(min = 1, max = 80)
    @Pattern(regexp = RegularExpressions.MAIN_DETAIL_ADDRESS, message = ValidationFailureMessages.MAIN_DETAIL_ADDRESS)
    private String detailPlace;
}
