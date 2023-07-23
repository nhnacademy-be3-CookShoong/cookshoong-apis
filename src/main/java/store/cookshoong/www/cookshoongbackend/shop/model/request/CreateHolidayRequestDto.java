package store.cookshoong.www.cookshoongbackend.shop.model.request;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 휴업일 등록 Dto.
 *
 * @author papel (윤동현)
 * @since 2023.07.07
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateHolidayRequestDto {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayStartDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate holidayEndDate;
}
