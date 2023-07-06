package store.cookshoong.www.cookshoongbackend.address.model.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import store.cookshoong.www.cookshoongbackend.common.util.RegularExpressions;
import store.cookshoong.www.cookshoongbackend.common.util.ValidationFailureMessages;

/**
 * 매장에서 주소를 등록할 때 사용되는 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Getter
@NoArgsConstructor
public class CreateAddressRequestDto {

    @NotBlank
    @Length(min = 1, max = 80)
    @Pattern(regexp = RegularExpressions.LETTER_WITH_NUMBER, message = ValidationFailureMessages.LETTER_WITH_NUMBER)
    private String mainPlace;

    @Length(min = 1, max = 80)
    @Pattern(regexp = RegularExpressions.LETTER_WITH_NUMBER, message = ValidationFailureMessages.LETTER_WITH_NUMBER)
    private String detailPlace;

    @NotBlank
    private BigDecimal latitude;

    @NotBlank
    private BigDecimal longitude;
}
