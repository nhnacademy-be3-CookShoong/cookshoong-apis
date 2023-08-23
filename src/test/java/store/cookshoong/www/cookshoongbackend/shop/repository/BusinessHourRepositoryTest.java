package store.cookshoong.www.cookshoongbackend.shop.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.shop.entity.BusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.DayType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.BusinessHourRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 영업시간 확인하는 테스트코드.
 *
 * @author seungyeon
 * @since 2023.08.12
 */
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class,
    TestEntityManager.class,
    TestPersistEntity.class})
class BusinessHourRepositoryTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    TestPersistEntity tpe;
    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity testEntity;

    @Autowired
    BusinessHourRepository businessHourRepository;

    @Test
    void lookupBusinessHourByDayCodeTest(){
        Store store = tpe.getOpenStoreByOneAccount(tpe.getLevelOneActiveBusiness());
        DayType monday = new DayType("MON", "월요일");
        DayType saturday = new DayType("SAT", "토요일");

        em.persist(monday);
        em.persist(saturday);

        BusinessHour businessHour = new BusinessHour(store, monday, LocalTime.of(10,10,10), LocalTime.of(12,10,10));
        BusinessHour businessHour1 = new BusinessHour(store, monday, LocalTime.of(13,10,10), LocalTime.of(20,10,10));

        BusinessHour satBusinessHour = new BusinessHour(store, saturday, LocalTime.of(10,10,10), LocalTime.of(20,10,10));

        businessHourRepository.save(businessHour);
        businessHourRepository.save(businessHour1);
        businessHourRepository.save(satBusinessHour);

        boolean expectTrue = businessHourRepository.lookupBusinessHourByDayCode(store.getId(),
            LocalDate.of(2023,8,12).getDayOfWeek().name().substring(0,3),
            LocalTime.of(16,0,0));

        boolean expectFalse = businessHourRepository.lookupBusinessHourByDayCode(store.getId(),
            LocalDate.of(2023,8,11).getDayOfWeek().name().substring(0,3),
            LocalTime.of(16,0,0));
        boolean expectTrue2 = businessHourRepository.lookupBusinessHourByDayCode(store.getId(),
            LocalDate.of(2023,8,14).getDayOfWeek().name().substring(0,3),
            LocalTime.of(16,0,0));


        assertThat(expectTrue).isTrue();
        assertThat(expectFalse).isFalse();
        assertThat(expectTrue2).isTrue();
    }
}
