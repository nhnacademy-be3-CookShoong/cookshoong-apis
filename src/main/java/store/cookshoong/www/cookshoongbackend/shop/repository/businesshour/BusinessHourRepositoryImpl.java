package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.shop.entity.QBusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;

/**
 * 영업시간 커스텀 레포지토리 구현.
 *
 * @author papel (윤동현)
 * @since 2023.07.10
 */
@RequiredArgsConstructor
public class BusinessHourRepositoryImpl implements BusinessHourRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<SelectBusinessHourResponseDto> lookupBusinessHourPage(Long storeId, Pageable pageable) {
        List<SelectBusinessHourResponseDto> responseDtos = getBusinessHours(storeId, pageable);
        long total = getTotal(storeId);
        return new PageImpl<>(responseDtos, pageable, total);
    }

    /**
     * 매장의 영업시간 리스트.
     *
     * @param storeId 매장 아이디
     * @param pageable  페이지 정보
     * @return 각 페이지에 해당하는 영업시간 리스트
     */
    public List<SelectBusinessHourResponseDto> getBusinessHours(Long storeId, Pageable pageable) {
        QBusinessHour businessHour = QBusinessHour.businessHour;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(Projections.constructor(SelectBusinessHourResponseDto.class,
                businessHour.id, store.id, businessHour.dayCode, businessHour.openHour, businessHour.closeHour))
            .from(businessHour)
            .innerJoin(businessHour.store, store)
            .where(businessHour.store.id.eq(storeId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    /**
     * 매장의 영업시간 리스트의 총 개수.
     *
     * @param storeId 매장 아이디
     * @return 매장이 등록한 영업시간의 총 개수
     */
    private Long getTotal(Long storeId) {
        QBusinessHour businessHour = QBusinessHour.businessHour;
        QStore store = QStore.store;

        return jpaQueryFactory
            .select(businessHour.count())
            .from(businessHour)
            .innerJoin(businessHour.store, store)
            .where(businessHour.store.id.eq(storeId))
            .fetchOne();
    }
}
