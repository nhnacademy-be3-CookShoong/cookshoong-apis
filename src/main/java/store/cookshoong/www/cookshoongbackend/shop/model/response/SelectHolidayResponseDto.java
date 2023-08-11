package store.cookshoong.www.cookshoongbackend.shop.model.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import lombok.Getter;

/**
 * 휴업일 리스트 응답을 위한 Dto.
 *
 * @author papel (윤동현)
 * @since 2023.07.07
 */
@Getter
public class SelectHolidayResponseDto {
    private final Long id;
    private final LocalDate holidayStartDate;
    private final LocalDate holidayEndDate;

    /**
     * 사업자 회원이 본인 매장 휴무일 조회.
     *
     * @param holidayStartDate 휴무일 시작 날짜
     * @param holidayEndDate   휴무일 종료 날짜
     */
    @QueryProjection
    public SelectHolidayResponseDto(Long id, LocalDate holidayStartDate, LocalDate holidayEndDate) {
        this.id = id;
        this.holidayStartDate = holidayStartDate;
        this.holidayEndDate = holidayEndDate;
    }
}
