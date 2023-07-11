package store.cookshoong.www.cookshoongbackend.store.repository.businesshour;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.store.entity.BusinessHour;
import store.cookshoong.www.cookshoongbackend.store.entity.DayType;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import(TestPersistEntity.class)
class BusinessHourRepositoryTest {

    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    BusinessHourRepository businessHourRepository;

    @Autowired
    TestPersistEntity tpe;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("영업시간 조회 - 성공")
    void findByStore_Id() {
        Store store = tpe.getOpenStore();
        DayType dayType = new DayType("MON", "월요일");
        BusinessHour actual = new BusinessHour(store, dayType, LocalTime.of(9,00), LocalTime.of(21,00));
        em.persist(dayType);
        businessHourRepository.save(actual);
        em.clear();

        List<BusinessHour> expect = businessHourRepository.findByStore_Id(store.getId());
        Assertions.assertThat(expect).hasSize(1);
    }
}
