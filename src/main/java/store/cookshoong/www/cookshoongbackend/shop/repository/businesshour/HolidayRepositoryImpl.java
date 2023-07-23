package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.shop.entity.QHoliday;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;

/**
 * 휴업일 커스텀 레포지토리 구현
 *
 * @author papel (윤동현)
 * @since 2023.07.07
 */
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectHolidayResponseDto> lookupHolidayPage(Long storeId, Pageable pageable) {
        List<SelectHolidayResponseDto> responseDtos = getHolidays(storeId, pageable);
        long total = getTotal(storeId);
        return new PageImpl<>(responseDtos, pageable, total);
    }

    /**
     * 매장의 휴업일 리스트.
     *
     * @param storeId 매장 아이디
     * @param pageable  페이지 정보
     * @return 각 페이지에 해당하는 휴업일 리스트
     */
    private List<SelectHolidayResponseDto> getHolidays(Long storeId, Pageable pageable) {
        QHoliday holiday = QHoliday.holiday;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(Projections.constructor(SelectHolidayResponseDto.class,
                holiday.id, store.id, holiday.holidayStartDate, holiday.holidayEndDate))
            .from(holiday)
            .innerJoin(holiday.store, store)
            .where(holiday.store.id.eq(storeId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    /**
     * 매장의 휴업일 리스트의 총 개수.
     *
     * @param storeId 매장 아이디
     * @return 매장이 등록한 휴업일의 총 개수
     */
    private Long getTotal(Long storeId) {
        QHoliday holiday = QHoliday.holiday;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(holiday.count())
            .from(holiday)
            .innerJoin(holiday.store, store)
            .where(holiday.store.id.eq(storeId))
            .fetchOne();
    }
}
