package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import store.cookshoong.www.cookshoongbackend.shop.entity.QBusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.QDayType;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
import store.cookshoong.www.cookshoongbackend.shop.model.response.QSelectBusinessHourResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;

/**
 * 영업시간 커스텀 레포지토리 구현.
 *
 * @author papel (윤동현)
 * @contributor seungyeon (유승연)
 * @since 2023.07.10
 */
@RequiredArgsConstructor
public class BusinessHourRepositoryImpl implements BusinessHourRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 매장의 영업시간 리스트.
     *
     * @param storeId 매장 아이디
     * @return 각 페이지에 해당하는 영업시간 리스트
     */
    @Override
    public List<SelectBusinessHourResponseDto> lookupBusinessHours(Long storeId) {
        QBusinessHour businessHour = QBusinessHour.businessHour;
        QDayType dayType = QDayType.dayType;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(new QSelectBusinessHourResponseDto(
                businessHour.id, dayType.dayCode, dayType.description, businessHour.openHour, businessHour.closeHour))
            .from(businessHour)
            .innerJoin(businessHour.store, store)
            .innerJoin(businessHour.dayCode, dayType)
            .where(businessHour.store.id.eq(storeId))
            .orderBy(businessHour.openHour.asc())
            .fetch();
    }

    /**
     * {@inheritDoc}
     *
     * @param storeId the store id
     * @param dayCode 요일 코드
     * @param nowTime 현재 시간
     * @return 영업시간 해당 여부
     */
    @Override
    public boolean lookupBusinessHourByDayCode(Long storeId, String dayCode, LocalTime nowTime) {
        QBusinessHour businessHour = QBusinessHour.businessHour;
        QDayType dayType = QDayType.dayType;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(businessHour.id)
            .from(businessHour)
            .innerJoin(businessHour.store, store)
            .innerJoin(businessHour.dayCode, dayType)
            .where(businessHour.store.id.eq(storeId)
                .and(businessHour.dayCode.dayCode.eq(dayCode))
                .and((businessHour.openHour.loe(nowTime))
                .and(businessHour.closeHour.goe(nowTime))))
            .fetchFirst() != null;
    }
}
