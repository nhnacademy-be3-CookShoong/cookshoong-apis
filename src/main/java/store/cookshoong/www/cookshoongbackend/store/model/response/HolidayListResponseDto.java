package store.cookshoong.www.cookshoongbackend.store.model.response;

import java.time.LocalDate;
import lombok.Getter;

/**
 * 휴업일 리스트 응답을 위한 Dto.
 *
 * @author papel
 * @since 2023.07.07
 */
@Getter
public class HolidayListResponseDto {
    private LocalDate holidayDate;
}
