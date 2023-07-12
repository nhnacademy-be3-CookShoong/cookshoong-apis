package store.cookshoong.www.cookshoongbackend.store.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalTime;

/**
 * 영업시간 리스트 응답을 위한 Dto
 *
 * @author papel
 * @since 2023.07.10
 */
public class SelectBusinessHourResponseDto {
    private String dayCodeName;

    private LocalTime openHour;

    private LocalTime closeHour;

    /**
     * 사업자 회원이 본인 매장 영업시간 조회.
     *
     * @param dayCodeName           요일 이름
     * @param openHour              영업 시작 시간
     * @param closeHour             영업 종료 시간
     */
    @QueryProjection
    public SelectBusinessHourResponseDto(String dayCodeName, LocalTime openHour, LocalTime closeHour) {
        this.dayCodeName = dayCodeName;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }
}
