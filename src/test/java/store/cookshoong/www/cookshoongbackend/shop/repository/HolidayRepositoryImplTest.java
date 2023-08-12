package store.cookshoong.www.cookshoongbackend.shop.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.service.LocalFileService;
import store.cookshoong.www.cookshoongbackend.shop.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.HolidayRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 휴무일을 관리하는 테스트코드.
 *
 * @author seungyeon
 * @since 2023.08.12
 */
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class,
    TestEntityManager.class,
    TestPersistEntity.class})
class HolidayRepositoryImplTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;
    @Autowired
    TestPersistEntity tpe;
    @Autowired
    TestEntityManager em;

    @Autowired
    TestEntity testEntity;

    @Autowired
    HolidayRepository holidayRepository;

    @Test
    @DisplayName("매장마다 휴무일인지 체크하기 - 성공(해당날짜에만 true)")
    void lookupHolidayByStoreId() {
        Store store = tpe.getOpenStoreByOneAccount(tpe.getLevelOneActiveBusiness());
        Holiday holiday = new Holiday(store, LocalDate.of(2023, 8,11), LocalDate.of(2023,8,12));

        holidayRepository.save(holiday);

        boolean isHoliday = holidayRepository.lookupHolidayByStoreId(store.getId(), LocalDate.of(2023,8,12));
        boolean isHoliday2 = holidayRepository.lookupHolidayByStoreId(store.getId(), LocalDate.of(2023,8,11));
        boolean isHoliday3 = holidayRepository.lookupHolidayByStoreId(store.getId(), LocalDate.of(2023,8,13));
        boolean isHoliday4 = holidayRepository.lookupHolidayByStoreId(store.getId(), LocalDate.of(2023,8,10));

        assertThat(isHoliday).isTrue();
        assertThat(isHoliday2).isTrue();

        assertThat(isHoliday3).isFalse();
        assertThat(isHoliday4).isFalse();
    }
}
