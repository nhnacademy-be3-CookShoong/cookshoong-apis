package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.shop.entity.BusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.DayType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 영업시간 에 관한 테스트.
 *
 * @author papel
 * @since 2023.07.14
 */
@DataJpaTest
@Import(TestPersistEntity.class)
class BusinessHourRepositoryTest {

    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    BusinessHourRepository businessHourRepository;

    @Autowired
    TestEntityManager em;

    @Autowired
    TestPersistEntity tpe;

    @Test
    @DisplayName("영업시간 저장 - 성공")
    void saveBusinessHour() {
        Store store = tpe.getOpenStore();
        DayType dayType = new DayType("WED", "수요일");
        BusinessHour expected =
            new BusinessHour(
                store, dayType,
                LocalTime.of(9, 0),
                LocalTime.of(21, 0));

        em.persist(store);
        em.persist(dayType);

        Long businessHourId = businessHourRepository.save(expected).getId();

        em.clear();
    }
}
