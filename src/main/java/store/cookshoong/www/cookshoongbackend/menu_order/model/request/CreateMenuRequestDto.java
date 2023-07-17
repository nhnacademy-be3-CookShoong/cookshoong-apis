package store.cookshoong.www.cookshoongbackend.menu_order.model.request;

import java.math.BigDecimal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 등록 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateMenuRequestDto {
    @NotBlank
    private String name;
    @NotNull
    private Integer price;
    private String description;
    private String image;
    @NotNull
    private Integer cookingTime;
    @Digits(integer = 3, fraction = 1)
    private BigDecimal earningRate;
}
