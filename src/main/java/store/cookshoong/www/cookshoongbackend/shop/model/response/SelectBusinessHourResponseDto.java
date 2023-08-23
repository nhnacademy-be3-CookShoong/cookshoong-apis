package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalTime;
import lombok.Getter;

/**
 * 영업시간 리스트 응답을 위한 Dto
 *
 * @author papel (윤동현)
 * @since 2023.07.10
 */
@Getter
public class SelectBusinessHourResponseDto {
    private final Long id;
    private final String dayCode;
    private final String dayCodeName;
    private final LocalTime openHour;
    private final LocalTime closeHour;

    /**
     * 사업자 회원이 본인 매장 영업시간 조회.
     *
     * @param dayCodeName 요일 이름
     * @param openHour    영업 시작 시간
     * @param closeHour   영업 종료 시간
     */
    @QueryProjection
    public SelectBusinessHourResponseDto(Long id, String dayCode, String dayCodeName, LocalTime openHour, LocalTime closeHour) {
        this.id = id;
        this.dayCode = dayCode;
        this.dayCodeName = dayCodeName;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }
}
