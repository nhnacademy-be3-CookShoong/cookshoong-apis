package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.shop.entity.QHoliday;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectHolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;

/**
 * 휴업일 커스텀 레포지토리 구현.
 *
 * @author papel (윤동현)
 * @contributor seongyeon(유승연)
 * @since 2023.07.07
 */
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 매장의 휴업일 리스트.
     *
     * @param storeId 매장 아이디
     * @return 각 페이지에 해당하는 휴업일 리스트
     */
    @Override
    public List<SelectHolidayResponseDto> lookupHolidays(Long storeId) {
        QHoliday holiday = QHoliday.holiday;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectHolidayResponseDto(
                holiday.id, holiday.holidayStartDate, holiday.holidayEndDate))
            .from(holiday)
            .innerJoin(holiday.store, store)
            .where(holiday.store.id.eq(storeId))
            .fetch();
    }

    @Override
    public boolean lookupHolidayByStoreId(Long storeId, LocalDate nowDate) {
        QStore store = QStore.store;
        QHoliday holiday = QHoliday.holiday;

        return jpaQueryFactory
            .select(holiday.id)
            .from(holiday)
            .innerJoin(holiday.store, store)
            .where(
                holiday.store.id.eq(storeId)
                    .and(holiday.holidayStartDate.loe(nowDate).and(holiday.holidayEndDate.goe(nowDate)))
            )
            .fetchFirst() != null;
    }
}
