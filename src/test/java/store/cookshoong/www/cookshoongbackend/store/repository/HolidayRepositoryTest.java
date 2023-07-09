package store.cookshoong.www.cookshoongbackend.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.store.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.repository.holiday.HolidayRepository;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

@DataJpaTest
@Import(TestPersistEntity.class)
class HolidayRepositoryTest {

    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    TestPersistEntity tpe;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("휴업일 조회 - 성공")
    void find_holiday() {
        Store store = tpe.getOpenStore();
        Holiday actual = new Holiday(store, LocalDate.of(2020, 2, 20));
        holidayRepository.save(actual);

        em.clear();

        List<Holiday> expect = holidayRepository.findByStore_Id(store.getId());
        Assertions.assertThat(expect).hasSize(1);
    }


}
