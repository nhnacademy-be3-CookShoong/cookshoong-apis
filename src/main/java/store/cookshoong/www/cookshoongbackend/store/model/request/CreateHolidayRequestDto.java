package store.cookshoong.www.cookshoongbackend.store.model.request;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 휴업일 등록 Dto.
 *
 * @author papel
 * @since 2023.07.07
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateHolidayRequestDto {

    @NotNull
    private LocalDate holidayStartDate;

    @NotNull
    private LocalDate holidayEndDate;
}
