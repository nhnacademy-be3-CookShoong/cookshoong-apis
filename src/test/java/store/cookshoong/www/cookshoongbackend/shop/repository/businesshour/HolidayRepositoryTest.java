package store.cookshoong.www.cookshoongbackend.shop.repository.businesshour;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.shop.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 휴업일 에 관한 테스트.
 *
 * @author papel
 * @since 2023.07.14
 */
@DataJpaTest
@Import(TestPersistEntity.class)
class HolidayRepositoryTest {

    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    TestEntityManager em;

    @Autowired
    TestPersistEntity tpe;

    @Test
    @DisplayName("휴업일 저장 - 성공")
    void saveHoliday() {
        Store store = tpe.getOpenStore();
        Holiday expected =
            new Holiday(
                store,
                LocalDate.of(2023,9, 10),
                LocalDate.of(2023, 9, 22));

        em.persist(store);

        Long holidayId = holidayRepository.save(expected).getId();

        em.clear();
    }


}
