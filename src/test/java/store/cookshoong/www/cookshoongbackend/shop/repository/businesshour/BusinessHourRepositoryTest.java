package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.cookshoong.www.cookshoongbackend.shop.entity.BusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.DayType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

@DataJpaTest
class BusinessHourRepositoryTest {

    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    BusinessHourRepository businessHourRepository;

    @Test
    @DisplayName("영업시간 조회 - 성공")
    void saveBusinessHour() {
        Store store = Mockito.mock(Store.class);
        DayType dayType = Mockito.mock(DayType.class);
        BusinessHour actual =
            new BusinessHour(
                store, dayType,
                LocalTime.of(9, 0),
                LocalTime.of(21, 0));
    }
}
