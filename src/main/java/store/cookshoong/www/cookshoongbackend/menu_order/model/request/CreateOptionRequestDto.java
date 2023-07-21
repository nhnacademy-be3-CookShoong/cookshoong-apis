package store.cookshoong.www.cookshoongbackend.menu_order.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 옵션 등록 Dto.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOptionRequestDto {
    @NotBlank
    private String name;
    @NotNull
    private Integer price;
}
