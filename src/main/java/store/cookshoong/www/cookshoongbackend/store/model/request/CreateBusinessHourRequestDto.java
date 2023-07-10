package store.cookshoong.www.cookshoongbackend.store.model.request;

import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 영업시간 등록 Dto.
 *
 * @author papel
 * @since 2023.07.10
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateBusinessHourRequestDto {

    private String dayCodeName;

    private LocalTime openHour;

    private LocalTime closeHour;
}
