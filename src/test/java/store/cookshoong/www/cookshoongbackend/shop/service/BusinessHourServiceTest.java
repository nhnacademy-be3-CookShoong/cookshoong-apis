package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.BusinessHourRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.HolidayRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 매장 영업시간 및 상태 관리 하는 서비스 코드 테스트.
 *
 * @author seungyeon (유승연)
 * @since 2023.08.12
 */
@ExtendWith(MockitoExtension.class)
class BusinessHourServiceTest {

    @Mock
    private BusinessHourRepository businessHourRepository;

    @Mock
    private HolidayRepository holidayRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreStatusRepository storeStatusRepository;
    @Spy
    TestEntity te = new TestEntity();
    @InjectMocks
    TestPersistEntity tpe;
    @InjectMocks
    private BusinessHourService businessHourService;

    @Test
    @DisplayName("해당 매장 휴무일일 경우 BreakTime으로 상태 변경")
    void updateStoreStatusByTimer_holiday() {
        Store store = tpe.getOpenStore();
        StoreStatus storeStatus = new StoreStatus("BREAK_TIME", "휴식중");
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeStatusRepository.getReferenceById(anyString())).thenReturn(storeStatus);
        when(holidayRepository.lookupHolidayByStoreId(store.getId(), LocalDate.now())).thenReturn(true);

        Store result = businessHourService.updateStoreStatusByTimer(store.getId());

        assertThat(result.getStoreStatus().getCode()).isEqualTo(StoreStatus.StoreStatusCode.BREAK_TIME.name());
    }

    @Test
    @DisplayName("휴무일이 아니면서 영업시간일 경우")
    void updateStoreStatusByTimer_() {
        Store store = tpe.getOpenStore();
        StoreStatus storeStatus = new StoreStatus("OPEN", "영업중");
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(holidayRepository.lookupHolidayByStoreId(store.getId(), LocalDate.now())).thenReturn(false);
        when(businessHourRepository.lookupBusinessHourByDayCode(eq(store.getId()), anyString(), any(LocalTime.class))).thenReturn(true);
        when(storeStatusRepository.getReferenceById(anyString())).thenReturn(storeStatus);
        Store result = businessHourService.updateStoreStatusByTimer(store.getId());

        assertThat(result.getStoreStatus().getCode()).isEqualTo(StoreStatus.StoreStatusCode.OPEN.name());
    }
}
