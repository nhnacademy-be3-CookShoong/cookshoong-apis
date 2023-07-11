package store.cookshoong.www.cookshoongbackend.menu.model.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 옵션 그룹 등록 Dto.
 *
 * @author papel
 * @since 2023.07.11
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOptionGroupRequestDto {
    @NotBlank
    private String name;
    private Integer minSelectCount;
    private Integer maxSelectCount;
}
